package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.LongestStreak;
import net.chess_platform.match_service.model.PlayerMmr;

public interface PlayerStatsMapper {

    @Mapping(target = "joinedAt", source = "playerMmr.createdAt")
    @Mapping(target = "lastPlayedAt", source = "playerMmr.lastPlayed")
    @Mapping(target = "mmr", source = "playerMmr.rankedMmr")
    PlayerStatsDto toDto(Leaderboard leaderboard, PlayerMmr playerMmr, List<LongestStreak> longestStreaks);
}
