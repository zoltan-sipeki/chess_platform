package net.chess_platform.chat_service.authorization;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class PrivacyAuthorizationService {

    public enum Action {
        PRIVACY_READ,
        PRIVACY_UPDATE
    }

    public Authorization authorizePrivacyRead(CurrentUser user) {
        var auth = new Authorization();
        auth.setAction(Action.PRIVACY_READ);
        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Privacy.class, new MongoQueryFragment<>(Criteria.where("userId").is(user.id())));
        } else {
            auth.setQueryCondition(Privacy.class, new MongoQueryFragment.False<>());
        }
        return auth;
    }

    public Authorization authorizePrivacyUpdate(CurrentUser user) {
        var auth = new Authorization();
        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Privacy.class, new MongoQueryFragment<>(Criteria.where("userId").is(user.id())));
        } else {
            auth.setQueryCondition(Privacy.class, new MongoQueryFragment.False<>());
        }
        return auth;
    }
}
