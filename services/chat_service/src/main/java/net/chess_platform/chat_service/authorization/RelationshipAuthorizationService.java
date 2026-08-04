package net.chess_platform.chat_service.authorization;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.policy.PolicyUtils;
import net.chess_platform.common.security.CurrentUser;

@Service
public class RelationshipAuthorizationService {

    public enum Action {
        RELATIONSHIP_QUERY,
        CONTACTS_QUERY,
    }

    public Authorization authorizeRelationshipQuery(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.RELATIONSHIP_QUERY);

        auth.setAllowed(PolicyUtils.or(
                () -> user.hasRole("chess_application.user"),
                () -> user.hasRole("cp_chat_service.cp_match_service")));

        return auth;
    }

    public Authorization authorizeContactsQuery(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.CONTACTS_QUERY);

        auth.setAllowed(() -> user.hasRole("cp_chat_service.cp_relay_service"));

        return auth;
    }
}
