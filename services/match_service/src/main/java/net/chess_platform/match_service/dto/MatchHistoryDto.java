package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchHistoryDto(
		UUID matchId,
		String matchType,
		OffsetDateTime startedAt,
		long duration,
		String color,
		String outcome,
		int mmrChange) {
}
