package net.chess_platform.chat_service.authorization;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.FriendRequest;
import net.chess_platform.chat_service.model.FriendRequest.Status;
import net.chess_platform.chat_service.model.Privacy.Restriction.Resource;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class FriendAuthorizationService {

    public enum Action {
        FRIEND_REQUEST_QUERY,
        FRIEND_REQUEST_CREATE,
        FRIEND_REQUEST_UPDATE,
        FRIEND_QUERY,
        UNFRIEND
    }

    private final PrivacyRepository privacyRepository;

    private final FriendRepository friendRepository;

    public FriendAuthorizationService(PrivacyRepository privacyRepository, FriendRepository friendRepository) {
        this.privacyRepository = privacyRepository;
        this.friendRepository = friendRepository;
    }

    public Authorization authorizeFriendRequestRead(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.FRIEND_REQUEST_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(FriendRequest.class, new MongoQueryFragment<>(
                    Criteria.where("receiver").is(user.id()).and("status").is(Status.PENDING)));
        } else {
            auth.setQueryCondition(FriendRequest.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeFriendRequestCreate(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.FRIEND_REQUEST_CREATE);

        auth.setAllowed(() -> user.hasRole("chess_application.user"));

        return auth;
    }

    public Authorization authorizeFriendRequestUpdate(CurrentUser user, UUID friendRequestId) {
        var auth = new Authorization();

        auth.setAction(Action.FRIEND_REQUEST_UPDATE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(FriendRequest.class, new MongoQueryFragment<>(
                    Criteria.where("_id").is(friendRequestId)
                            .and("receiver").is(user.id())
                            .and("status").is(Status.PENDING)));
        } else {
            auth.setQueryCondition(FriendRequest.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeFriendRead(CurrentUser user) {
        var auth = new Authorization();
        auth.setAction(Action.FRIEND_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Friend.class,
                    new MongoQueryFragment<>(Criteria.where("user").is(user.id())));
        } else {
            auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeFriendRead(CurrentUser user, UUID userId) {
        var auth = new Authorization();
        auth.setAction(Action.FRIEND_QUERY);

        BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
            if (condition) {
                auth.setQueryCondition(Friend.class, new MongoQueryFragment<>(
                        Criteria.where("user").is(userId)));
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

        var privacy = privacyRepository.findOne(userId);
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
    }

    public Authorization authorizeUnfriend(CurrentUser user, UUID userId) {
        var auth = new Authorization();

        auth.setAction(Action.UNFRIEND);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Friend.class,
                    new MongoQueryFragment<>(Criteria.where(null).orOperator(
                            Criteria.where("user").is(userId).and("friend").is(user.id()),
                            Criteria.where("user").is(user.id()).and("friend").is(userId))));
        } else {
            auth.setQueryCondition(Friend.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }
}
