package net.chess_platform.match_service.permission;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.AbstractPermissionService;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.FalseJPAQueryFragment;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.integration.ChatServiceProxy;
import net.chess_platform.match_service.model.MatchDetail;
import net.chess_platform.match_service.model.MatchStat;
import net.chess_platform.match_service.model.OngoingMatch;
import net.chess_platform.match_service.model.PlayerMmr;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

@Service
public class PermissionService extends AbstractPermissionService<Action> {

    public enum Action {
        MATCH_HISTORY_QUERY,

        MATCH_STATS_QUERY,

        PRIVACY_SETTING_UPDATE,

        ONGOING_MATCH_QUERY,
        ONGOING_MATCH_CREATE,

        MMR_QUERY
    }

    private PrivacySettingRepository privacySettingRepository;

    private ChatServiceProxy chatServiceProxy;

    public PermissionService(PrivacySettingRepository privacySettingRepository, ChatServiceProxy chatServiceProxy) {
        this.privacySettingRepository = privacySettingRepository;
        this.chatServiceProxy = chatServiceProxy;
    }

    @Override
    protected void registerPolicies() {
        registerPolicy(Action.MATCH_HISTORY_QUERY, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var auth = new Authorization();

            auth.setAction(Action.MATCH_HISTORY_QUERY);

            BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
                if (condition) {
                    auth.setQueryCondition(MatchDetail.class, new JPAQueryFragment<>((root, query, cb) -> {
                        return cb.equal(root.get("user").get("id"), userId);
                    }));
                } else {
                    auth.setQueryCondition(MatchDetail.class, new FalseJPAQueryFragment<>());
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

            var ps = privacySettingRepository.findByUserIdAndResource(userId,
                    PrivacySetting.Resource.MATCH_HISTORY);

            switch (ps.getRestriction()) {
                case PUBLIC:
                    rules.accept(auth, true);
                    break;
                case PRIVATE:
                    rules.accept(auth, user.id().equals(userId));
                    break;
                case FRIENDS:
                    rules.accept(auth, chatServiceProxy.areFriends(user.id(), userId));
                    break;
            }

            return auth;
        });

        registerPolicy(Action.MATCH_STATS_QUERY, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var ps = privacySettingRepository.findByUserIdAndResource((UUID) userId,
                    PrivacySetting.Resource.MATCH_STATS);

            var auth = new Authorization();

            auth.setAction(Action.MATCH_STATS_QUERY);

            BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
                if (condition) {
                    auth.setQueryCondition(MatchStat.class, new JPAQueryFragment<>((root, query, cb) -> {
                        return cb.equal(root.get("user").get("id"), userId);
                    }));
                } else {
                    auth.setQueryCondition(MatchStat.class, new FalseJPAQueryFragment<>());
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

            switch (ps.getRestriction()) {
                case PUBLIC:
                    rules.accept(auth, true);
                    break;
                case PRIVATE:
                    rules.accept(auth, user.id().equals(userId));
                    break;
                case FRIENDS:
                    rules.accept(auth, chatServiceProxy.areFriends(user.id(), userId));
                    break;
            }

            return auth;
        });

        registerPolicy(Action.PRIVACY_SETTING_UPDATE, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.PRIVACY_SETTING_UPDATE);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(PrivacySetting.class,
                        new JPAQueryFragment<>((root, query, cb) -> cb.equal(root.get("user").get("id"), user.id())));
            } else {
                auth.setQueryCondition(PrivacySetting.class, new FalseJPAQueryFragment<>());
            }

            return auth;
        });

        registerPolicy(Action.ONGOING_MATCH_QUERY, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.ONGOING_MATCH_QUERY);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(OngoingMatch.class,
                        new JPAQueryFragment<>((root, query, cb) -> cb.equal(root.get("user").get("id"), user.id())));
                return auth;
            }

            if (user.hasRole("cp_match_service.cp_matchmaking_service")) {
                var userId = (UUID) attributes.get("userId");
                if (userId == null) {
                    auth.setQueryCondition(OngoingMatch.class, new FalseJPAQueryFragment<>());
                } else {
                    auth.setQueryCondition(OngoingMatch.class,
                            new JPAQueryFragment<>((root, query, cb) -> cb.equal(root.get("user").get("id"), userId)));
                }
            }

            return auth;
        });

        registerPolicy(Action.ONGOING_MATCH_CREATE, (user, attributes) -> {
            var auth = new Authorization();

            auth.setAction(Action.ONGOING_MATCH_CREATE);

            auth.setAllowed(() -> user.hasRole("cp_match_service.cp_chess_service"));
            auth.setCreateCheck(OngoingMatch.class, entity -> true);
            return auth;
        });

        registerPolicy(Action.MMR_QUERY, (user, attributes) -> {
            var userId = (UUID) attributes.get("userId");

            var auth = new Authorization();

            auth.setAction(Action.MMR_QUERY);

            if (user.hasRole("chess_application.user")) {
                auth.setQueryCondition(PlayerMmr.class,
                        new JPAQueryFragment<>((root, query, cb) -> cb.equal(root.get("user").get("id"), userId)));
            } else {
                auth.setQueryCondition(PlayerMmr.class, new FalseJPAQueryFragment<>());
            }

            return auth;
        });
    }

}
