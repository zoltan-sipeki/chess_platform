package net.chess_platform.match_service.authorization;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.model.PrivacySetting;

@Service
public class PrivacyAuthorizationService {

    public enum Action {
        PRIVACY_SETTING_UPDATE
    }

    public Authorization authorizePrivacySettingUpdate(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.PRIVACY_SETTING_UPDATE);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(PrivacySetting.class,
                    new JPAQueryFragment<>((from, cb) -> cb.equal(from.get("player").get("id"), user.id())));
        } else {
            auth.setQueryCondition(PrivacySetting.class, new JPAQueryFragment.False<>());
        }

        return auth;
    }
}
