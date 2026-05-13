package net.chess_platform.match_service.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.dto.OngoingMatchRequest;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchDetail;
import net.chess_platform.match_service.model.OngoingMatch;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T17:28:24+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public List<MatchHistoryDto> toMatchHistoryList(List<MatchDetail> matchDetails) {
        if ( matchDetails == null ) {
            return null;
        }

        List<MatchHistoryDto> list = new ArrayList<MatchHistoryDto>( matchDetails.size() );
        for ( MatchDetail matchDetail : matchDetails ) {
            list.add( toMatchHistory( matchDetail ) );
        }

        return list;
    }

    @Override
    public MatchHistoryDto toMatchHistory(MatchDetail matchResponse) {
        if ( matchResponse == null ) {
            return null;
        }

        UUID matchId = null;
        String matchType = null;
        OffsetDateTime startedAt = null;
        long duration = 0L;
        String color = null;
        String score = null;
        int mmrChange = 0;

        matchId = matchResponseMatchId( matchResponse );
        Match.Type type = matchResponseMatchType( matchResponse );
        if ( type != null ) {
            matchType = type.name();
        }
        startedAt = matchResponseMatchStartedAt( matchResponse );
        duration = matchResponseMatchDuration( matchResponse );
        if ( matchResponse.getColor() != null ) {
            color = matchResponse.getColor().name();
        }
        if ( matchResponse.getScore() != null ) {
            score = matchResponse.getScore().name();
        }
        if ( matchResponse.getMmrChange() != null ) {
            mmrChange = matchResponse.getMmrChange();
        }

        MatchHistoryDto matchHistoryDto = new MatchHistoryDto( matchId, matchType, startedAt, duration, color, score, mmrChange );

        return matchHistoryDto;
    }

    @Override
    public OngoingMatchDto toDto(OngoingMatch ongoingMatch) {
        if ( ongoingMatch == null ) {
            return null;
        }

        long matchId = 0L;
        String target = null;

        matchId = ongoingMatch.getMatchId();
        target = ongoingMatch.getTarget();

        UUID userId = null;

        OngoingMatchDto ongoingMatchDto = new OngoingMatchDto( matchId, userId, target );

        return ongoingMatchDto;
    }

    @Override
    public List<OngoingMatchDto> toDto(List<OngoingMatch> ongoingMatches) {
        if ( ongoingMatches == null ) {
            return null;
        }

        List<OngoingMatchDto> list = new ArrayList<OngoingMatchDto>( ongoingMatches.size() );
        for ( OngoingMatch ongoingMatch : ongoingMatches ) {
            list.add( toDto( ongoingMatch ) );
        }

        return list;
    }

    @Override
    public List<OngoingMatch> toModelList(List<OngoingMatchRequest> ongoingMatchRequest) {
        if ( ongoingMatchRequest == null ) {
            return null;
        }

        List<OngoingMatch> list = new ArrayList<OngoingMatch>( ongoingMatchRequest.size() );
        for ( OngoingMatchRequest ongoingMatchRequest1 : ongoingMatchRequest ) {
            list.add( ongoingMatchRequestToOngoingMatch( ongoingMatchRequest1 ) );
        }

        return list;
    }

    private UUID matchResponseMatchId(MatchDetail matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getId();
    }

    private Match.Type matchResponseMatchType(MatchDetail matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getType();
    }

    private OffsetDateTime matchResponseMatchStartedAt(MatchDetail matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getStartedAt();
    }

    private long matchResponseMatchDuration(MatchDetail matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return 0L;
        }
        return match.getDuration();
    }

    protected OngoingMatch ongoingMatchRequestToOngoingMatch(OngoingMatchRequest ongoingMatchRequest) {
        if ( ongoingMatchRequest == null ) {
            return null;
        }

        OngoingMatch ongoingMatch = new OngoingMatch();

        ongoingMatch.setMatchId( ongoingMatchRequest.matchId() );
        ongoingMatch.setTarget( ongoingMatchRequest.target() );

        return ongoingMatch;
    }
}
