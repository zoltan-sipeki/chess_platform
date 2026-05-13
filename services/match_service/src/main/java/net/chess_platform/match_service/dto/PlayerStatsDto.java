package net.chess_platform.match_service.dto;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

public record PlayerStatsDto(
        int rank,
        int mmr,
        float percentile,
        List<LongestStreakDto> longestStreaks,
        Instant joinedAt,
        OffsetDateTime lastPlayedAt) {

}
