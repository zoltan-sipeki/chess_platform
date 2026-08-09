package net.chess_platform.matchmaking_service.service;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_service.model.MatchRouting;

@Service
public class MatchmakingAuthorizationService {

    public enum Action {
        MATCH_ROUTING_UPDATE
    }

    public Authorization authorizeMatchRoutingUpdate(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.MATCH_ROUTING_UPDATE);

        if (user.hasRole("cp_matchmaking_service.cp_chess_service")) {
            auth.setQueryCondition(MatchRouting.class, new JPAQueryFragment.True<>());
        } else {
            auth.setQueryCondition(MatchRouting.class, new JPAQueryFragment.False<>());
        }
        return auth;
    }

}
