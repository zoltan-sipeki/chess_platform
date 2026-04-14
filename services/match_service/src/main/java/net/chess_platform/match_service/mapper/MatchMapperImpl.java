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
import net.chess_platform.match_service.model.MatchUser;
import net.chess_platform.match_service.model.OngoingMatch;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T22:01:55+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
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

        UUID userId = null;
        UUID matchId = null;
        String matchType = null;
        OffsetDateTime startedAt = null;
        OffsetDateTime endedAt = null;
        long duration = 0L;
        String color = null;
        String score = null;
        int mmrBefore = 0;
        int mmrAfter = 0;
        int mmrChange = 0;

        userId = matchResponseUserId( matchResponse );
        matchId = matchResponseMatchId( matchResponse );
        Match.Type type = matchResponseMatchType( matchResponse );
        if ( type != null ) {
            matchType = type.name();
        }
        startedAt = matchResponseMatchStartedAt( matchResponse );
        endedAt = matchResponseMatchEndedAt( matchResponse );
        duration = matchResponseMatchDuration( matchResponse );
        if ( matchResponse.getColor() != null ) {
            color = matchResponse.getColor().name();
        }
        if ( matchResponse.getScore() != null ) {
            score = matchResponse.getScore().name();
        }
        if ( matchResponse.getMmrBefore() != null ) {
            mmrBefore = matchResponse.getMmrBefore();
        }
        mmrAfter = matchResponse.getMmrAfter();
        if ( matchResponse.getMmrChange() != null ) {
            mmrChange = matchResponse.getMmrChange();
        }

        MatchHistoryDto matchHistoryDto = new MatchHistoryDto( userId, matchId, matchType, startedAt, endedAt, duration, color, score, mmrBefore, mmrAfter, mmrChange );

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

    private UUID matchResponseUserId(MatchDetail matchDetail) {
        MatchUser user = matchDetail.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
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

    private OffsetDateTime matchResponseMatchEndedAt(MatchDetail matchDetail) {
        Match match = matchDetail.getMatch();
        if ( match == null ) {
            return null;
        }
        return match.getEndedAt();
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
