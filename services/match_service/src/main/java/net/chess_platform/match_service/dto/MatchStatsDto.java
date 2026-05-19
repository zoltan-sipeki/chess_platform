package net.chess_platform.match_service.dto;

public record MatchStatsDto(
		String matchType,
		int gamesPlayed,
		int wins,
		int losses,
		int draws,
		float winRatio) {

}
