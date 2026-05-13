package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.LeaderboardEntryDto;
import net.chess_platform.match_service.model.Leaderboard;

// @Mapper(componentModel = "spring")
public interface LeaderboardMapper {

    @Mapping(target = "mmr", source = "leaderboard.rankedMmr")
    LeaderboardEntryDto toDto(Leaderboard leaderboard);

    List<LeaderboardEntryDto> toDtoList(List<Leaderboard> leaderboards);
}
