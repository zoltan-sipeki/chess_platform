package net.chess_platform.match_service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchResult;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-02T15:40:34+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MatchMapperImpl implements MatchMapper {

    @Override
    public List<MatchHistoryDto> toMatchHistoryList(List<MatchResult> matchDetails) {
        if ( matchDetails == null ) {
            return null;
        }

        List<MatchHistoryDto> list = new ArrayList<MatchHistoryDto>( matchDetails.size() );
        for ( MatchResult matchResult : matchDetails ) {
            list.add( toMatchHistory( matchResult ) );
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
        Instant startedAt = null;
        long duration = 0L;
        String color = null;
        String outcome = null;
        Integer mmrChange = null;

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
        if ( matchResponse.getMmrChange() != null && type != Match.Type.UNRANKED ) {
            mmrChange = matchResponse.getMmrChange();
        }

        MatchHistoryDto matchHistoryDto = new MatchHistoryDto( matchId, matchType, startedAt, duration, color, outcome, mmrChange );

        return matchHistoryDto;
    }

    private UUID matchResponseMatchId(MatchResult matchResult) {
        Match match = matchResult.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getId();
    }

    private Match.Type matchResponseMatchType(MatchResult matchResult) {
        Match match = matchResult.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getType();
    }

    private Instant matchResponseMatchStartedAt(MatchResult matchResult) {
        Match match = matchResult.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getStartedAt();
    }

    private long matchResponseMatchDuration(MatchResult matchResult) {
        Match match = matchResult.getMatch();
        if ( match == null ) {
            return 0L;
        }
        return match.getDuration();
    }
}
