package net.chess_platform.match_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.model.MatchStat;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T17:40:40+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MatchStatMapperImpl implements MatchStatMapper {

    @Override
    public MatchStatsDto toDto(MatchStat stat) {
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

        MatchStatsDto matchStatsDto = new MatchStatsDto( userId, matchType, gamesPlayed, wins, losses, draws, winRatio );

        return matchStatsDto;
    }

    @Override
    public List<MatchStatsDto> toDto(List<MatchStat> stat) {
        if ( stat == null ) {
            return null;
        }

        List<MatchStatsDto> list = new ArrayList<MatchStatsDto>( stat.size() );
        for ( MatchStat matchStat : stat ) {
            list.add( toDto( matchStat ) );
        }

        return list;
    }
}
