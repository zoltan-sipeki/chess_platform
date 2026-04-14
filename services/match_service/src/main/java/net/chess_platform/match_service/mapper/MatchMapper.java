package net.chess_platform.match_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.dto.OngoingMatchRequest;
import net.chess_platform.match_service.model.MatchDetail;
import net.chess_platform.match_service.model.OngoingMatch;

public interface MatchMapper {

    public List<MatchHistoryDto> toMatchHistoryList(List<MatchDetail> matchDetails);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "matchType", source = "match.type")
    @Mapping(target = "startedAt", source = "match.startedAt")
    @Mapping(target = "endedAt", source = "match.endedAt")
    @Mapping(target = "duration", source = "match.duration")
    public MatchHistoryDto toMatchHistory(MatchDetail matchResponse);

    public OngoingMatchDto toDto(OngoingMatch ongoingMatch);

    public List<OngoingMatchDto> toDto(List<OngoingMatch> ongoingMatches);

    public List<OngoingMatch> toModelList(List<OngoingMatchRequest> ongoingMatchRequest);
}
