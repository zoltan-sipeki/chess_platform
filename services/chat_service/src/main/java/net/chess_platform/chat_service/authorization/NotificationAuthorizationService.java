package net.chess_platform.chat_service.authorization;

import java.util.UUID;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.NotificationMetadata;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class NotificationAuthorizationService {

    public enum Action {
        NOTIFICATION_QUERY,
        NOTIFICATION_UPDATE,
        NOTIFICATION_DELETE
    }

    public Authorization authorizeNotificationQuery(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.NOTIFICATION_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Notification.class,
                    new MongoQueryFragment<>(Criteria.where("receiver").is(user.id())));
        } else {
            auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }

    public Authorization authorizeNotificationDelete(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.NOTIFICATION_DELETE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Notification.class,
                    new MongoQueryFragment<>(Criteria.where("receiver").is(user.id())));
        } else {
            auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
        }
        return auth;
    }

    public Authorization authorizeNotificationDelete(CurrentUser user, UUID notificationId) {
        var auth = new Authorization();

        auth.setAction(Action.NOTIFICATION_DELETE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Notification.class,
                    new MongoQueryFragment<>(Criteria.where("receiver").is(user.id()).and("_id").is(notificationId)));
        } else {
            auth.setQueryCondition(Notification.class, new MongoQueryFragment.False<>());
        }
        return auth;
    }

    public Authorization authorizeNotificationReadStateUpdate(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.NOTIFICATION_UPDATE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(NotificationMetadata.class,
                    new MongoQueryFragment<>(Criteria.where("receiver").is(user.id())));
        } else {
            auth.setQueryCondition(NotificationMetadata.class, new MongoQueryFragment.False<>());
        }

        return auth;
    }
}
