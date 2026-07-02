package net.chess_platform.match_service.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchResult;
import net.chess_platform.match_service.model.OngoingMatch;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T17:38:18+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public List<MatchHistoryDto> toMatchHistoryList(List<MatchResult> matchDetails) {
        if ( matchDetails == null ) {
            return null;
        }

        List<MatchHistoryDto> list = new ArrayList<MatchHistoryDto>( matchDetails.size() );
        for ( MatchResult matchDetail : matchDetails ) {
            list.add( toMatchHistory( matchDetail ) );
        }

        return list;
    }

    @Override
    public MatchHistoryDto toMatchHistory(MatchResult matchResponse) {
        if ( matchResponse == null ) {
            return null;
        }

        UUID matchId = null;
        String matchType = null;
        OffsetDateTime startedAt = null;
        long duration = 0L;
        String color = null;
        String outcome = null;
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
        if ( matchResponse.getOutcome() != null ) {
            outcome = matchResponse.getOutcome().name();
        }
        if ( matchResponse.getMmrChange() != null ) {
            mmrChange = matchResponse.getMmrChange();
        }

        MatchHistoryDto matchHistoryDto = new MatchHistoryDto( matchId, matchType, startedAt, duration, color, outcome, mmrChange );

        return matchHistoryDto;
    }

    private UUID matchResponseMatchId(MatchResult matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getId();
    }

    private Match.Type matchResponseMatchType(MatchResult matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getType();
    }

    private OffsetDateTime matchResponseMatchStartedAt(MatchResult matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getStartedAt();
    }

    private long matchResponseMatchDuration(MatchResult matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return 0L;
        }
        return match.getDuration();
    }

    protected OngoingMatch ongoingMatchDtoToOngoingMatch(OngoingMatchDto ongoingMatchDto) {
        if ( ongoingMatchDto == null ) {
            return null;
        }

        OngoingMatch ongoingMatch = new OngoingMatch();

        ongoingMatch.setMatchId( ongoingMatchDto.matchId() );
        ongoingMatch.setTarget( ongoingMatchDto.target() );

        return ongoingMatch;
    }
}
