package net.chess_platform.chat_service.service;

import static net.chess_platform.chat_service.model.Notification.Type.FRIEND_REQUEST_ACCEPTED;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.FriendAuthorizationService;
import net.chess_platform.chat_service.dto.FriendListDto;
import net.chess_platform.chat_service.dto.FriendRequestDto;
import net.chess_platform.chat_service.dto.UserDto;
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
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.chat_service.repository.FriendRequestRepository;
import net.chess_platform.chat_service.repository.NotificationMetadataRepository;
import net.chess_platform.chat_service.repository.NotificationRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.chat.NotificationEvent;
import net.chess_platform.common.domain_events.broker.chat.UnfriendEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;

    private final NotificationRepository notificationRepository;

    private final NotificationMetadataRepository notificationMetadataRepository;

    private final FriendRepository friendRepository;

    private final UserRepository userRepository;

    private final FriendAuthorizationService authService;

    private final DomainEventService eventService;

    private final NotificationMapper notificationMapper;

    private final UserMapper userMapper;

    private final FriendRequestMapper friendRequestMapper;

    public FriendService(
            FriendRequestRepository friendRequestRepository, NotificationRepository notificationRepository,
            NotificationMetadataRepository notificationMetadataRepository,
            FriendRepository friendRepository,
            ChannelMemberRepository channelMemberRepository,
            UserRepository userRepository,
            FriendAuthorizationService authService, DomainEventService eventService,
            NotificationMapper notificationMapper, UserMapper userMapper, FriendRequestMapper friendRequestMapper) {
        this.friendRequestRepository = friendRequestRepository;
        this.notificationRepository = notificationRepository;
        this.notificationMetadataRepository = notificationMetadataRepository;
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.eventService = eventService;
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
        this.friendRequestMapper = friendRequestMapper;
    }

    public UserDto createRequest(UUID receiverId, CurrentUser user) {
        var auth = authService.authorizeFriendRequestCreate(user);

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

        var r = friendRequestRepository.findPending(senderId, receiverId);

        if (r != null) {
            if (r.getSender().getId().equals(senderId)) {
                return null;
            }

            return updateStatus(r.getId(), Status.ACCEPTED, user);
        }

        var friendRequest = new FriendRequest();
        var sender = userRepository.findOne(senderId);

        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiverId);

        friendRequest = friendRequestRepository.save(friendRequest);
        if (friendRequest == null) {
            throw new AccessDeniedException();
        }

        var notification = new Notification();
        notification.setType(Notification.Type.FRIEND_REQUEST);
        notification.setSender(sender);
        notification.setReceiver(receiverId);
        notification.setFriendRequest(friendRequest.getId());
        notification.setSequenceNumber(notificationMetadataRepository.getNextSequenceNumber(receiverId));

        notificationRepository.save(notification);

        var event = new NotificationEvent(List.of(receiverId), notificationMapper.toEventPayload(notification));
        eventService.publish(event);

        return null;
    }

    public UserDto updateStatus(UUID friendRequestId, FriendRequest.Status status, CurrentUser user) {
        if (status == Status.PENDING) {
            throw new InvalidFriendRequestException("Status can be changed only to REJECTED or ACCEPTED");
        }

        var auth = authService.authorizeFriendRequestUpdate(user, friendRequestId);

        MongoQueryFragment<FriendRequest> fragment = auth.getQueryFragment(FriendRequest.class);

        var request = friendRequestRepository.updateStatus(fragment.getCriteria(), status);

        if (request == null) {
            throw new EntityNotFoundException();
        }

        if (status == Status.REJECTED) {
            notificationRepository.deleteByFriendRequestId(friendRequestId);
            return null;
        }

        var currentUserId = user.id();
        var receiver = request.getSender();

        var notification = new Notification();
        notification.setType(FRIEND_REQUEST_ACCEPTED);

        var sender = userRepository.findOne(currentUserId);

        notification.setSender(sender);
        notification.setReceiver(receiver.getId());
        notification.setSequenceNumber(notificationMetadataRepository.getNextSequenceNumber(receiver.getId()));

        notificationRepository.save(notification);
        notificationRepository.deleteByFriendRequestId(friendRequestId);

        friendRepository
                .save(List.of(new Friend(currentUserId, receiver.getId()),
                        new Friend(receiver.getId(), currentUserId)));

        var event = new NotificationEvent(List.of(receiver.getId()), notificationMapper.toEventPayload(notification));
        eventService.publish(event);

        return userMapper.toDto(receiver);
    }

    public List<FriendRequestDto> findAllRequests(CurrentUser user) {
        var auth = authService.authorizeFriendRequestRead(user);

        MongoQueryFragment<FriendRequest> fragment = auth.getQueryFragment(FriendRequest.class);

        var result = friendRequestRepository.findAll(fragment.getCriteria());

        return friendRequestMapper.toDtoList(result);
    }

    public FriendListDto findAllWithoutPresence(UUID userId, Pageable pageable, CurrentUser user) {
        var result = findAll(userId, pageable, user);
        return new FriendListDto(result.getTotalElements(),
                userMapper.toDtoListFromFriendWithoutPresence(result.getContent()));
    }

    public FriendListDto findAllWithPresence(UUID userId, Pageable pageable, CurrentUser user) {
        var result = findAll(userId, pageable, user);
        return new FriendListDto(result.getTotalElements(), userMapper.toDtoListFromFriend(result.getContent()));
    }

    public void unfriend(UUID friendId, CurrentUser user) {
        var auth = authService.authorizeUnfriend(user, friendId);

        MongoQueryFragment<Friend> fragment = auth.getQueryFragment(Friend.class);

        long deletedCount = friendRepository.deleteAll(fragment.getCriteria());

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }

        var event = new UnfriendEvent(List.of(friendId), new UnfriendEvent.Payload(user.id()));
        eventService.publish(event);
    }

    private Page<Friend> findAll(UUID userId, Pageable pageable, CurrentUser user) {
        var auth = authService.authorizeFriendRead(user, userId);

        MongoQueryFragment<Friend> fragment = auth.getQueryFragment(Friend.class);

        return friendRepository.findAll(fragment.getCriteria(), pageable);
    }
}
