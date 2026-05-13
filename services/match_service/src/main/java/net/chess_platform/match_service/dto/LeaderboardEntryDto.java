package net.chess_platform.match_service.dto;

public record LeaderboardEntryDto(UserDto player, int rank, int rankedMmr, float percentile) {
}