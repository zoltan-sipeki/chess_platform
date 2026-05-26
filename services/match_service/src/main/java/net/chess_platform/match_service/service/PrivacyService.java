package net.chess_platform.match_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PrivacyDto;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

@Service
public class PrivacyService {

    private final PrivacySettingRepository privacySettingRepository;

    public PrivacyService(PrivacySettingRepository privacySettingRepository) {
        this.privacySettingRepository = privacySettingRepository;
    }

    @Transactional
    public void updatePrivacySettings(PrivacyDto privacy, CurrentUser user) {
        if (privacy.matchHistory() != null) {
            privacySettingRepository.updateMatchHistory(user.id(), privacy.matchHistory());
        }

        if (privacy.matchStats() != null) {
            privacySettingRepository.updateMatchStats(user.id(), privacy.matchStats());
        }

        if (privacy.playerStats() != null) {
            privacySettingRepository.updatePlayerStats(user.id(), privacy.playerStats());
        }
    }

    public PrivacyDto getPrivacySettings(CurrentUser currentUser) {
        var settings = privacySettingRepository.findByPlayerId(currentUser.id());

        PrivacySetting.Restriction playerStats = null;
        PrivacySetting.Restriction matchStats = null;
        PrivacySetting.Restriction matchHistory = null;

        for (var p : settings) {
            switch (p.getResource()) {
                case PrivacySetting.Resource.PLAYER_STATS -> playerStats = p.getRestriction();
                case PrivacySetting.Resource.MATCH_STATS -> matchStats = p.getRestriction();
                case PrivacySetting.Resource.MATCH_HISTORY -> matchHistory = p.getRestriction();
            }
        }

        return new PrivacyDto(playerStats, matchStats, matchHistory);
    }
}
