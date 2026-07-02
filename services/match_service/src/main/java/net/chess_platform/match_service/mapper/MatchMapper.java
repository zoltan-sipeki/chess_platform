package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.model.MatchResult;

// @Mapper(componentModel = "spring")
public interface MatchMapper {

    public List<MatchHistoryDto> toMatchHistoryList(List<MatchResult> matchDetails);

    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "matchType", source = "match.type")
    @Mapping(target = "startedAt", source = "match.startedAt")
    @Mapping(target = "duration", source = "match.duration")
    public MatchHistoryDto toMatchHistory(MatchResult matchResponse);
}
