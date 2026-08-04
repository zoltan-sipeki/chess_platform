package net.chess_platform.chat_service.authorization;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.permission.policy.PolicyUtils;
import net.chess_platform.common.security.CurrentUser;

@Service
public class ChannelAuthorizationService {

    public enum Action {
        CHANNEL_QUERY,
        CHANNEL_CREATE,
        CHANNEL_UPDATE_NAME,
        CHANNEL_ADD_MEMBER,
        CHANNEL_KICK_MEMBER,
        CHANNEL_UPDATE_LAST_READ_MESSAGE,
        CHANNEL_CLEAR_HISTORY,
        CHANNEL_BROADCAST_TYPING,
        CHANNEL_LEAVE
    }

    private final ChannelMemberRepository channelMemberRepository;

    public ChannelAuthorizationService(ChannelMemberRepository channelMemberRepository) {
        this.channelMemberRepository = channelMemberRepository;
    }

    public Authorization authorizeChannelCreate(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_CREATE);

        auth.setAllowed(() -> user.hasRole("chess_application.user"));

        return auth;
    }

    public Authorization authorizeChannelRead(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Channel.class,
                    new MongoQueryFragment<>(Criteria.where("memberIds").in(List.of(user.id()))));

        } else {
            auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
        }

        return auth;

    }

    public Authorization authorizeChannelRead(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Channel.class,
                    new MongoQueryFragment<>(
                            Criteria.where("_id").is(channelId).and("memberIds").in(List.of(user.id()))));
        } else {
            auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeClearChannelHistory(CurrentUser user, UUID channelId) {
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
    }

    public Authorization authorizeUpdateLastReadMessage(CurrentUser user, UUID channelId) {
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
    }

    public Authorization authorizeKickMember(CurrentUser user, UUID channelId, UUID kickedUserId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_KICK_MEMBER);

        if (user.hasRole("chess_application.user")
                && hasChannelRoles(user, channelId, List.of("OWNER", "MODERATOR"))) {
            auth.setQueryCondition(ChannelMember.class,
                    new MongoQueryFragment<>(
                            Criteria.where("userId").is(kickedUserId).and("channel.id").is(channelId)
                                    .and("channel.type").is(Channel.Type.GROUP)));
        } else {
            auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeAddMember(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_ADD_MEMBER);

        auth.setAllowed(PolicyUtils.and(
                () -> user.hasRole("chess_application.user"),
                () -> hasChannelRoles(user, channelId, List.of("OWNER", "MODERATOR"))));

        return auth;
    }

    public Authorization authorizeLeaveChannel(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_LEAVE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment<>(
                    Criteria.where("userId").is(user.id()).and("channel.id").is(channelId).and("channel.type")
                            .is(Channel.Type.GROUP)));
        } else {
            auth.setQueryCondition(ChannelMember.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeUpdateChannelName(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_UPDATE_NAME);

        if (user.hasRole("chess_application.user") && hasChannelRoles(user, channelId, List.of("OWNER"))) {
            auth.setQueryCondition(Channel.class, new MongoQueryFragment<>(Criteria.where("_id").is(channelId)));
        } else {
            auth.setQueryCondition(Channel.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeBroadcastTyping(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.CHANNEL_BROADCAST_TYPING);

        auth.setAllowed(PolicyUtils.and(
                () -> user.hasRole("chess_application.user"),
                () -> channelMemberRepository.isInChannel(user.id(), channelId)));

        return auth;
    }

    private boolean hasChannelRoles(CurrentUser user, UUID channelId, List<String> roles) {
        return channelMemberRepository.hasChannelRoles(user.id(), channelId, roles);
    }

}
