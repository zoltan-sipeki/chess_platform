package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.LongestStreak;
import net.chess_platform.match_service.model.Player;

// @Mapper(componentModel = "spring")
public interface PlayerStatsMapper {

    @Mapping(target = "joinedAt", source = "player.createdAt")
    @Mapping(target = "lastPlayedAt", source = "player.lastPlayedAt")
    @Mapping(target = "mmr", source = "player.rankedMmr")
    PlayerStatsDto toDto(Leaderboard leaderboard, Player player, List<LongestStreak> longestStreaks);
}
