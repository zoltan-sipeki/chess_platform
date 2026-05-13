package net.chess_platform.match_service.dto;

public record LeaderboardEntryDto(PlayerDto player, int rank, int mmr, float percentile) {
}