package net.chess_platform.match_service.authorization;

import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.policy.PolicyUtils;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.integration.ChatServiceProxy;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

@Service
public class PlayerAuthorizationService {

    public enum Action {
        PLAYER_STATS_QUERY
    }

    private final PrivacySettingRepository privacySettingRepository;

    private final ChatServiceProxy chatServiceProxy;

    public PlayerAuthorizationService(PrivacySettingRepository privacySettingRepository,
            ChatServiceProxy chatServiceProxy) {
        this.privacySettingRepository = privacySettingRepository;
        this.chatServiceProxy = chatServiceProxy;
    }

    public Authorization authorizePlayerStatsQuery(CurrentUser user, UUID userId) {
        var ps = privacySettingRepository.findByUserIdAndResource((UUID) userId,
                PrivacySetting.Resource.PLAYER_STATS);

        var auth = new Authorization();

        auth.setAction(Action.PLAYER_STATS_QUERY);

        auth.setAllowed(PolicyUtils.and(
                () -> user.hasRole("chess_application.user"),
                () -> {
                    switch (ps.getRestriction()) {
                        case PUBLIC:
                            return true;
                        case PRIVATE:
                            return user.id().equals(userId);
                        case FRIENDS:
                            return chatServiceProxy.areFriends(user.id(), userId);
                    }

                    return false;
                }));

        return auth;
    }
}
