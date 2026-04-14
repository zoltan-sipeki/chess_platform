package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchHistoryDto(
		UUID userId,
		UUID matchId,
		String matchType,
		OffsetDateTime startedAt,
		OffsetDateTime endedAt,
		long duration,
		String color,
		String score,
		int mmrBefore,
		int mmrAfter,
		int mmrChange) {
}
