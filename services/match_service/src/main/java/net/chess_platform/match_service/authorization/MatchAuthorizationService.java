package net.chess_platform.match_service.authorization;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.FalseJPAQueryFragment;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.common.permission.TrueJPAQueryFragment;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.integration.ChatServiceProxy;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.MatchResult;
import net.chess_platform.match_service.model.MatchStat;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

@Service
public class MatchAuthorizationService {

    public enum Action {
        MATCH_HISTORY_QUERY,
        MATCH_STATS_QUERY,
        LEADERBOARD_QUERY
    }

    private final PrivacySettingRepository privacySettingRepository;

    private final ChatServiceProxy chatServiceProxy;

    public MatchAuthorizationService(PrivacySettingRepository privacySettingRepository,
            ChatServiceProxy chatServiceProxy) {
        this.privacySettingRepository = privacySettingRepository;
        this.chatServiceProxy = chatServiceProxy;
    }

    public Authorization authorizeMatchHistoryQuery(CurrentUser user, UUID userId) {
        var auth = new Authorization();

        auth.setAction(Action.MATCH_HISTORY_QUERY);

        BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
            if (condition) {
                auth.setQueryCondition(MatchResult.class, new JPAQueryFragment<>((root, query, cb) -> {
                    return cb.equal(root.get("player").get("id"), userId);
                }));
            } else {
                auth.setQueryCondition(MatchResult.class, new FalseJPAQueryFragment<>());
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
    }

    public Authorization authorizeMatchStatsQuery(CurrentUser user, UUID userId) {
        var ps = privacySettingRepository.findByUserIdAndResource((UUID) userId,
                PrivacySetting.Resource.MATCH_STATS);

        var auth = new Authorization();

        auth.setAction(Action.MATCH_STATS_QUERY);

        BiConsumer<Authorization, Boolean> rules = (a, condition) -> {
            if (condition) {
                auth.setQueryCondition(MatchStat.class, new JPAQueryFragment<>((root, query, cb) -> {
                    return cb.equal(root.get("player").get("id"), userId);
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
    }

    public Authorization authorizeLeaderboardQuery(CurrentUser user) {
        var auth = new Authorization();

        auth.setAction(Action.LEADERBOARD_QUERY);

        if (user.hasRole("chess_application.user")) {
            auth.setQueryCondition(Leaderboard.class, new TrueJPAQueryFragment<>());
        } else {
            auth.setQueryCondition(Leaderboard.class, new FalseJPAQueryFragment<>());
        }

        return auth;
    }
}
