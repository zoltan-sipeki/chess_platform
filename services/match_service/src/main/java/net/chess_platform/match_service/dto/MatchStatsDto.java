package net.chess_platform.match_service.dto;

import java.util.UUID;

public record MatchStatsDto(
		UUID userId,
		String matchType,
		int gamesPlayed,
		int wins,
		int losses,
		int draws,
		float winRatio) {

}
