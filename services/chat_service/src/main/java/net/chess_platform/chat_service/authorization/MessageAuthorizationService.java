package net.chess_platform.chat_service.authorization;

import java.util.UUID;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.permission.policy.PolicyUtils;
import net.chess_platform.common.security.CurrentUser;

@Service
public class MessageAuthorizationService {

    public enum Action {
        MESSAGE_CREATE,
        MESSAGE_QUERY,
        MESSAGE_UPDATE_CONTENT,
        MESSAGE_DELETE
    }

    private final ChannelMemberRepository channelMemberRepository;

    public MessageAuthorizationService(ChannelMemberRepository channelMemberRepository) {
        this.channelMemberRepository = channelMemberRepository;
    }

    public Authorization authorizeMessageCreate(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.MESSAGE_CREATE);

        auth.setAllowed(PolicyUtils.and(
                () -> user.hasRole("chess_application.user"),
                () -> channelMember(user, channelId)));

        return auth;
    }

    public Authorization authorizeMessageRead(CurrentUser user, UUID channelId) {
        var auth = new Authorization();

        auth.setAction(Action.MESSAGE_QUERY);

        if (user.hasRole("chess_application.user") && channelMember(user, channelId)) {
            auth.setQueryCondition(Message.class,
                    new MongoQueryFragment<>(Criteria.where("channelId").is(channelId)));
        } else {
            auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeMessageUpdateContent(CurrentUser user, UUID messageId) {
        var auth = new Authorization();

        auth.setAction(Action.MESSAGE_UPDATE_CONTENT);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Message.class, new MongoQueryFragment<>(
                    Criteria.where("senderId").is(user.id()).and("messageId").is(messageId)));
        } else {
            auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeMessageDelete(CurrentUser user, UUID messageId) {
        var auth = new Authorization();

        auth.setAction(Action.MESSAGE_DELETE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Message.class,
                    new MongoQueryFragment<>(Criteria.where("senderId").is(user.id())
                            .and("messageId").is(messageId)));
        } else {
            auth.setQueryCondition(Message.class, new MongoQueryFragment.False<>());
        }

        return auth;

    }

    private boolean channelMember(CurrentUser user, UUID channelId) {
        return channelMemberRepository.isInChannel(user.id(), channelId);
    }
}
