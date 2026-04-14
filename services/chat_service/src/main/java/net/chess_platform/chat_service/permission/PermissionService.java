package net.chess_platform.chat_service.permission;

import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.Notification.FriendRequestDetails;
import net.chess_platform.chat_service.model.Privacy.Restriction.Resource;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.common.permission.AbstractPermissionService;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.permission.policy.PolicyUtils;
import net.chess_platform.common.security.CurrentUser;

@Service
public class PermissionService extends AbstractPermissionService<Action> {

    public enum Action {
        CHANNEL_QUERY,
        CHANNEL_CREATE,
        CHANNEL_UPDATE_NAME,
        CHANNEL_ADD_MEMBER,
        CHANNEL_KICK_MEMBER,
        CHANNEL_UPDATE_LAST_READ_MESSAGE,
        CHANNEL_CLEAR_HISTORY,
        CHANNEL_LEAVE,

        FRIEND_REQUEST_CREATE,
        FRIEND_REQUEST_UPDATE_STATUS,

        UNFRIEND,
        FRIEND_QUERY,

        MESSAGE_QUERY,
        MESSAGE_CREATE,
        MESSAGE_UPDATE_CONTENT,
        MESSAGE_DELETE,

        NOTIFICATION_QUERY,
        NOTIFICATION_DELETE,

        CONTACTS_QUERY_ALL,

        RELATIONSHIP_QUERY
    }

    private ChannelMemberRepository channelMemberRepository;

    private PrivacyRepository privacyRepository;

    private FriendRepository friendRepository;

    public PermissionService(ChannelMemberRepository channelMemberRepository, PrivacyRepository privacyRepository,
            FriendRepository friendRepository) {
        this.channelMemberRepository = channelMemberRepository;
        this.privacyRepository = privacyRepository;
        this.friendRepository = friendRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerPolicies() {
        registerPolicy(Action.CHANNEL_QUERY, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_QUERY);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(Channel.class,
                        new MongoQueryFragment<>(Criteria.where("memberIds").in(List.of(user.id()))));
            } else {
                auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.CHANNEL_CREATE, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_CREATE);

            auth.setAllowed(() -> user.hasRole("chess_application.user"));

            auth.setCreateCheck(Channel.class, entity -> true);
            auth.setCreateCheck(ChannelMember.class,
                    entity -> true);

            return auth;
        });

        registerPolicy(Action.CHANNEL_UPDATE_NAME, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_UPDATE_NAME);

            if (user.hasRole("chess_application.user") && hasChannelRoles(user, channelId, List.of("OWNER"))) {
                auth.setQueryCondition(Channel.class, new MongoQueryFragment<>(Criteria.where("_id").is(channelId)));
            } else {
                auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.CHANNEL_ADD_MEMBER, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_ADD_MEMBER);

            auth.setAllowed(PolicyUtils.and(
                    () -> user.hasRole("chess_application.user"),
                    () -> hasChannelRoles(user, channelId, List.of("OWNER", "MODERATOR"))));

            auth.setCreateCheck(ChannelMember.class, entity -> entity.getChannel().getId().equals(channelId));
            auth.setCreateCheck(Channel.class, entity -> entity.getId().equals(channelId));
            return auth;
        });

        registerPolicy(Action.CHANNEL_KICK_MEMBER, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");
            var kickedUserId = (UUID) attributes.get("userId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_KICK_MEMBER);

            if (user.hasRole("chess_application.user") && hasChannelRoles(user, channelId, List.of("OWNER", "MODERATOR"))) {
                auth.setQueryCondition(ChannelMember.class,
                        new MongoQueryFragment<>(
                                Criteria.where("userId").is(kickedUserId).and("channel.id").is(channelId)));
                auth.setQueryCondition(Channel.class,
                        new MongoQueryFragment<>(Criteria.where("_id").is(channelId).and("memberIds")
                                .in(List.of(user.id(), kickedUserId))));
            } else {
                auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
                auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.CHANNEL_UPDATE_LAST_READ_MESSAGE, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_UPDATE_LAST_READ_MESSAGE);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(ChannelMember.class,
                        new MongoQueryFragment<>(
                                Criteria.where("userId").is(user.id()).and("channel.id").is(channelId)));
            } else {
                auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.CHANNEL_CLEAR_HISTORY, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_CLEAR_HISTORY);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(ChannelMember.class,
                        new MongoQueryFragment<>(
                                Criteria.where("userId").is(user.id()).and("channel.id").is(channelId)));

            } else {
                auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
            }
            return auth;
        });

        registerPolicy(Action.CHANNEL_LEAVE, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.CHANNEL_LEAVE);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment<>(
                        Criteria.where("userId").is(user.id()).and("channel.id").is(channelId)));
                auth.setQueryCondition(Channel.class, new MongoQueryFragment<>(
                        Criteria.where("_id").is(channelId).and("memberIds")
                                .in(List.of(user.id()))));
            } else {
                auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
                auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.FRIEND_REQUEST_CREATE, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.FRIEND_REQUEST_CREATE);

            auth.setAllowed(() -> user.hasRole("chess_application.user"));

            auth.setCreateCheck(Notification.class, entity -> true);

            return auth;
        });

        registerPolicy(Action.FRIEND_REQUEST_UPDATE_STATUS, (user, attributes) -> {
            var friendRequestId = (UUID) attributes.get("friendRequestId");

            var auth = new Authorization();

            auth.setAction(Action.FRIEND_REQUEST_UPDATE_STATUS);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(Notification.class, new MongoQueryFragment<>(
                        Criteria.where("_id").is(friendRequestId)
                                .and("receiverId").is(user.id())
                                .and("details.status").is(FriendRequestDetails.Status.PENDING)));
            } else {
                auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.FRIEND_QUERY, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var auth = new Authorization();
            auth.setAction(Action.FRIEND_QUERY);

            if (userId == null) {
                if (user.hasRole("chess_application.user")) {
                    auth.setQueryCondition(Friend.class,
                            new MongoQueryFragment<>(Criteria.where("userId").is(user.id())));
                } else {
                    auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
                }

                return auth;
            }

            BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
                if (condition) {
                    auth.setQueryCondition(Friend.class, new MongoQueryFragment<>(
                            Criteria.where("userId").is(userId)));
                } else {
                    auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
                }
            };

            if (!user.hasRole("chess_application.user")) {
                rules.accept(auth, false);
                return auth;
            }

            if (user.id().equals(userId)) {
                rules.accept(auth, true);
                return auth;
            }

            var privacy = privacyRepository.findByUserId(userId);
            var restriction = privacy.findRestriction(Resource.FRIENDS);

            switch (restriction.getSetting()) {
                case PUBLIC:
                    rules.accept(auth, true);
                    break;
                case PRIVATE:
                    rules.accept(auth, false);
                    break;
                case FRIENDS:
                    rules.accept(auth, friendRepository.areFriends(user.id(), userId));
                    break;
            }

            return auth;
        });

        registerPolicy(Action.UNFRIEND, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var auth = new Authorization();

            auth.setAction(Action.UNFRIEND);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(Friend.class,
                        new MongoQueryFragment<>(Criteria.where(null).orOperator(
                                Criteria.where("userId").is(userId).and("friendId").is(user.id()),
                                Criteria.where("userId").is(user.id()).and("friendId").is(userId))));
            } else {
                auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.MESSAGE_CREATE, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.MESSAGE_CREATE);

            auth.setAllowed(PolicyUtils.and(
                    () -> user.hasRole("chess_application.user"),
                    () -> channelMember(user, channelId)));

            auth.setCreateCheck(Message.class, entity -> entity.getChannelId().equals(channelId));

            return auth;
        });

        registerPolicy(Action.MESSAGE_QUERY, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");

            var auth = new Authorization();

            auth.setAction(Action.MESSAGE_QUERY);

            if (user.hasRole("chess_application.user") && channelMember(user, channelId)) {
                auth.setQueryCondition(Message.class,
                        new MongoQueryFragment<>(Criteria.where("channelId").is(channelId)));
            } else {
                auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.MESSAGE_UPDATE_CONTENT, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");
            var messageId = (long) attributes.get("messageId");

            var auth = new Authorization();

            auth.setAction(Action.MESSAGE_UPDATE_CONTENT);

            if (user.hasRole("chess_application.user") && channelMember(user, channelId)) {
                auth.setQueryCondition(Message.class, new MongoQueryFragment<>(
                        Criteria.where("senderId").is(user.id()).and("messageId").is(messageId)));
            } else {
                auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.MESSAGE_DELETE, (user, attributes) -> {
            var channelId = (UUID) attributes.get("channelId");
            var messageId = (long) attributes.get("messageId");

            var auth = new Authorization();

            auth.setAction(Action.MESSAGE_DELETE);

            if (user.hasRole("chess_application.user") && channelMember(user, channelId)) {
                auth.setQueryCondition(Message.class,
                        new MongoQueryFragment<>(Criteria.where("senderId").is(user.id()).and("channelId").is(channelId)
                                .and("messageId").is(messageId)));
            } else {
                auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.NOTIFICATION_QUERY, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.NOTIFICATION_QUERY);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(Notification.class,
                        new MongoQueryFragment<>(Criteria.where("receiverId").is(user.id())));
            } else {
                auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.NOTIFICATION_DELETE, (user, attributes) -> {
            var notificationId = (UUID) attributes.get("notificationId");

            var auth = new Authorization();

            auth.setAction(Action.NOTIFICATION_DELETE);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(Notification.class,
                        new MongoQueryFragment<>(
                                Criteria.where("_id").is(notificationId).and("receiverId").is(user.id())));
            } else {
                auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
            }
            return auth;
        });

        registerPolicy(Action.CONTACTS_QUERY_ALL, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var auth = new Authorization();

            auth.setAction(Action.CONTACTS_QUERY_ALL);

            if (user.hasRole("cp_chat_service.cp_relay_service")) {
                auth.setQueryCondition(Friend.class, new MongoQueryFragment<>(Criteria.where("userId").is(userId)));
                auth.setQueryCondition(Channel.class,
                        new MongoQueryFragment<>(Criteria.where("memberIds").is(userId)));
            } else {
                auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
                auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });

        registerPolicy(Action.RELATIONSHIP_QUERY, (user, attributes) -> {
            var userIds = (List<UUID>) attributes.get("userIds");

            var auth = new Authorization();

            auth.setAction(Action.RELATIONSHIP_QUERY);

            if (user.hasRole("cp_chat_service.cp_match_service")) {
                auth.setAllowed(() -> true);
                auth.setQueryCondition(Friend.class, new MongoQueryFragment<>(
                        Criteria.where("userId").is(userIds.get(0)).and("friendId").is(userIds.get(1))));
            } else {
                auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
            }

            return auth;
        });
    }

    private boolean hasChannelRoles(CurrentUser user, UUID channelId, List<String> roles) {
        return channelMemberRepository.hasChannelRoles(user.id(), channelId, roles);
    }

    private boolean channelMember(CurrentUser user, UUID channelId) {
        return channelMemberRepository.isInChannel(user.id(), channelId);
    }
}
