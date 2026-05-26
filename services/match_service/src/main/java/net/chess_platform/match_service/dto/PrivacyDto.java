package net.chess_platform.match_service.dto;

import net.chess_platform.match_service.model.PrivacySetting;

public record PrivacyDto(
        PrivacySetting.Restriction playerStats,
        PrivacySetting.Restriction matchStats,
        PrivacySetting.Restriction matchHistory) {
}
