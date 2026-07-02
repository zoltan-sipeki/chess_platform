package net.chess_platform.matchmaking_service.service;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.AbstractPermissionService;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.FalseJPAQueryFragment;
import net.chess_platform.common.permission.TrueJPAQueryFragment;
import net.chess_platform.matchmaking_service.model.MatchRouting;
import net.chess_platform.matchmaking_service.service.PermissionService.Action;

@Service
public class PermissionService extends AbstractPermissionService<Action> {

    public enum Action {
        MATCH_ROUTING_UPDATE
    }

    @Override
    protected void registerPolicies() {
        registerPolicy(Action.MATCH_ROUTING_UPDATE, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.MATCH_ROUTING_UPDATE);

            if (user.hasRole("cp_matchmaking_service.cp_chess_service")) {
                auth.setQueryCondition(MatchRouting.class, new TrueJPAQueryFragment<>());
            } else {
                auth.setQueryCondition(MatchRouting.class, new FalseJPAQueryFragment<>());
            }
            return auth;
        });
    }

}
