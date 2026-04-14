package net.chess_platform.chat_service.service;

import static net.chess_platform.chat_service.model.Notification.FriendRequestDetails.Status.DECLINED;
import static net.chess_platform.chat_service.model.Notification.FriendRequestDetails.Status.PENDING;
import static net.chess_platform.chat_service.model.Notification.Type.FRIEND_REQUEST;
import static net.chess_platform.chat_service.model.Notification.Type.FRIEND_REQUEST_ACCEPTED;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.exception.InvalidFriendRequestException;
import net.chess_platform.chat_service.mapper.NotificationMapper;
import net.chess_platform.chat_service.mapper.UserMapper;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.Notification.FriendRequestDetails;
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
import net.chess_platform.common.dto.chat.UserDto;
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

    public FriendService(
            FriendRequestRepository friendRequestRepository, NotificationRepository notificationRepository,
            FriendRepository friendRepository,
            ChannelMemberRepository channelMemberRepository,
            UserRepository userRepository,
            PermissionService permissionService, DomainEventService eventService,
            NotificationMapper notificationMapper, UserMapper userMapper) {
        this.friendRequestRepository = friendRequestRepository;
        this.notificationRepository = notificationRepository;
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.eventService = eventService;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
    }

    public void createFriendRequest(UUID receiverId, CurrentUser user) {
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

        var friendRequest = new Notification();
        friendRequest.setType(FRIEND_REQUEST);
        friendRequest.setSenderId(senderId);
        friendRequest.setReceiverId(receiverId);
        friendRequest.setDetails(new FriendRequestDetails());

        friendRequest = friendRequestRepository.save(friendRequest, auth);
        if (friendRequest == null) {
            throw new AccessDeniedException();
        }

        var event = new NotificationEvent(List.of(receiverId), notificationMapper.toDto(friendRequest));
        eventService.publish(event);
    }

    public void updateFriendRequestStatus(UUID friendRequestId, FriendRequestDetails.Status status, CurrentUser user) {
        var auth = permissionService.authorize(Action.FRIEND_REQUEST_UPDATE_STATUS, user,
                Map.of("friendRequestId", friendRequestId));

        var request = friendRequestRepository.updateStatusForFriendRequest(status,
                auth);

        if (request == null) {
            throw new EntityNotFoundException();
        }

        if (status == DECLINED || status == PENDING) {
            return;
        }

        var currentUserId = user.id();
        var senderId = request.getSenderId();

        var notification = new Notification();
        notification.setType(FRIEND_REQUEST_ACCEPTED);
        notification.setSenderId(currentUserId);
        notification.setReceiverId(senderId);

        notificationRepository.save(notification);

        var sender = userRepository.findById(senderId);
        notification.setSender(sender);

        friendRepository
                .save(List.of(new Friend(currentUserId, senderId), new Friend(senderId, currentUserId)));

        var event = new NotificationEvent(List.of(senderId), notificationMapper.toDto(notification));
        eventService.publish(event);
    }

    public List<UserDto> findAll(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.FRIEND_QUERY, user,
                userId == null ? Map.of() : Map.of("userId", userId));
        var friends = friendRepository.findAll(auth);
        return userMapper.toDtoListFromFriend(friends);
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
