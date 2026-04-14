package net.chess_platform.match_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.MatchStatDto;
import net.chess_platform.match_service.model.MatchStat;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T22:01:56+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class MatchStatMapperImpl implements MatchStatMapper {

    @Override
    public MatchStatDto toDto(MatchStat stat) {
        if ( stat == null ) {
            return null;
        }

        String matchType = null;
        int gamesPlayed = 0;
        int wins = 0;
        int losses = 0;
        int draws = 0;
        float winRatio = 0.0f;

        if ( stat.getMatchType() != null ) {
            matchType = stat.getMatchType().name();
        }
        gamesPlayed = stat.getGamesPlayed();
        wins = stat.getWins();
        losses = stat.getLosses();
        draws = stat.getDraws();
        winRatio = stat.getWinRatio();

        UUID userId = null;

        MatchStatDto matchStatDto = new MatchStatDto( userId, matchType, gamesPlayed, wins, losses, draws, winRatio );

        return matchStatDto;
    }

    @Override
    public List<MatchStatDto> toDto(List<MatchStat> stat) {
        if ( stat == null ) {
            return null;
        }

        List<MatchStatDto> list = new ArrayList<MatchStatDto>( stat.size() );
        for ( MatchStat matchStat : stat ) {
            list.add( toDto( matchStat ) );
        }

        return list;
    }
}
