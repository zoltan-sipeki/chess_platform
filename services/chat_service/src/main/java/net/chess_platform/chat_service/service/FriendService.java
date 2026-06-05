package net.chess_platform.chat_service.service;

import static net.chess_platform.chat_service.model.Notification.Type.FRIEND_REQUEST_ACCEPTED;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.dto.FriendListDto;
import net.chess_platform.chat_service.dto.FriendRequestDto;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.exception.InvalidFriendRequestException;
import net.chess_platform.chat_service.mapper.FriendRequestMapper;
import net.chess_platform.chat_service.mapper.NotificationMapper;
import net.chess_platform.chat_service.mapper.UserMapper;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.FriendRequest;
import net.chess_platform.chat_service.model.FriendRequest.Status;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.chat_service.repository.FriendRequestRepository;
import net.chess_platform.chat_service.repository.NotificationRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.chat.NotificationEvent;
import net.chess_platform.common.domain_events.broker.chat.UnfriendEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.security.CurrentUser;

@Service
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;

    private final NotificationRepository notificationRepository;

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final PermissionService permissionService;

    private final DomainEventService eventService;

    private final NotificationMapper notificationMapper;

    private final UserMapper userMapper;

    private final FriendRequestMapper friendRequestMapper;

    public FriendService(
            FriendRequestRepository friendRequestRepository, NotificationRepository notificationRepository,
            FriendRepository friendRepository,
            ChannelMemberRepository channelMemberRepository,
            UserRepository userRepository,
            PermissionService permissionService, DomainEventService eventService,
            NotificationMapper notificationMapper, UserMapper userMapper, FriendRequestMapper friendRequestMapper) {
        this.friendRequestRepository = friendRequestRepository;
        this.notificationRepository = notificationRepository;
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.eventService = eventService;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.friendRequestMapper = friendRequestMapper;
    }

    public void createRequest(UUID receiverId, CurrentUser user) {
        var auth = permissionService.authorize(Action.FRIEND_REQUEST_CREATE, user, null);
        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        var senderId = user.id();

        if (senderId.equals(receiverId)) {
            throw new InvalidFriendRequestException("Cannot send friend request to yourself");
        }

        if (!userRepository.userExistsById(receiverId)) {
            throw new InvalidFriendRequestException("User does not exist");
        }

        if (friendRepository.areFriends(senderId, receiverId)) {
            throw new InvalidFriendRequestException("Already friends");
        }

        if (friendRequestRepository.hasPending(senderId, receiverId)) {
            return;
        }

        var friendRequest = new FriendRequest();
        var sender = new User();
        sender.setId(senderId);

        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiverId);

        friendRequest = friendRequestRepository.save(friendRequest, auth);
        if (friendRequest == null) {
            throw new AccessDeniedException();
        }

        var notification = new Notification();
        notification.setType(Notification.Type.FRIEND_REQUEST);
        notification.setSender(sender);
        notification.setReceiver(receiverId);
        notification.setFriendRequest(friendRequest.getId());
        notification.setSequenceNumber(notificationRepository.getNextSequenceNumber(receiverId));

        notificationRepository.save(notification);

        var event = new NotificationEvent(List.of(receiverId), notificationMapper.toEventPayload(notification));
        eventService.publish(event);
    }

    public void updateRequest(UUID friendRequestId, FriendRequest.Update update, CurrentUser user) {
        var status = update.getStatus();
        if (status == Status.PENDING) {
            throw new InvalidFriendRequestException("Status can be changed only to REJECTED or ACCEPTED");
        }

        var auth = permissionService.authorize(Action.FRIEND_REQUEST_UPDATE, user,
                Map.of("friendRequestId", friendRequestId));

        var request = friendRequestRepository.update(update,
                auth);

        if (request == null) {
            throw new EntityNotFoundException();
        }

        if (status == Status.REJECTED) {
            notificationRepository.deleteByFriendRequestId(friendRequestId);
            return;
        }

        var currentUserId = user.id();
        var receiver = request.getSender();

        var notification = new Notification();
        notification.setType(FRIEND_REQUEST_ACCEPTED);

        var sender = new User();
        sender.setId(currentUserId);

        notification.setSender(sender);
        notification.setReceiver(receiver.getId());
        notification.setSequenceNumber(notificationRepository.getNextSequenceNumber(receiver.getId()));

        notificationRepository.save(notification);
        notificationRepository.deleteByFriendRequestId(friendRequestId);

        friendRepository
                .save(List.of(new Friend(currentUserId, sender.getId()), new Friend(sender.getId(), currentUserId)));

        var event = new NotificationEvent(List.of(sender.getId()), notificationMapper.toEventPayload(notification));
        eventService.publish(event);
    }

    public List<FriendRequestDto> findAllRequests(CurrentUser user) {
        var auth = permissionService.authorize(Action.FRIEND_REQUEST_QUERY, user, null);
        var result = friendRequestRepository.findAll(auth);
        return friendRequestMapper.toDtoList(result);
    }

    public FriendListDto findAll(UUID userId, Pageable pageable, CurrentUser user) {
        var auth = permissionService.authorize(Action.FRIEND_QUERY, user,
                userId == null ? Map.of() : Map.of("userId", userId));
        var result = friendRepository.findAll(auth, pageable);
        return new FriendListDto(result.getTotalElements(), userMapper.toDtoListFromFriend(result.getContent()));
    }

    public void unfriend(UUID friendId, CurrentUser user) {
        var auth = permissionService.authorize(Action.UNFRIEND, user, Map.of("userId", friendId));
        long deletedCount = friendRepository.delete(auth);

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }

        var event = new UnfriendEvent(List.of(friendId), user.id());
        eventService.publish(event);
    }
}
