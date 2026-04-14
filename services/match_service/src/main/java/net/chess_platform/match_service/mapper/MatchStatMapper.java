package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import net.chess_platform.match_service.dto.MatchStatDto;
import net.chess_platform.match_service.model.MatchStat;

public interface MatchStatMapper {

    public MatchStatDto toDto(MatchStat stat);

    public List<MatchStatDto> toDto(List<MatchStat> stat);
}
