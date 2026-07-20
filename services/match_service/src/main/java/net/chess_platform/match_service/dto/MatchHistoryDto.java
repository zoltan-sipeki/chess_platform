package net.chess_platform.match_service.dto;

import java.time.Instant;
import java.util.UUID;

public record MatchHistoryDto(
		UUID matchId,
		String matchType,
		Instant startedAt,
		long duration,
		String color,
		String outcome,
		Integer mmrChange) {
}
