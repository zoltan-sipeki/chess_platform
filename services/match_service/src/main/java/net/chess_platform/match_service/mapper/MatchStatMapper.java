package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.model.MatchStat;

// @Mapper(componentModel = "spring")
public interface MatchStatMapper {

    public MatchStatsDto toDto(MatchStat stat);

    public List<MatchStatsDto> toDto(List<MatchStat> stat);
}
