package net.chess_platform.match_service.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.LongestStreakDto;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.LongestStreak;
import net.chess_platform.match_service.model.PlayerMmr;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-12T22:35:45+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class PlayerStatsMapperImpl implements PlayerStatsMapper {

    @Override
    public PlayerStatsDto toDto(Leaderboard leaderboard, PlayerMmr playerMmr, List<LongestStreak> longestStreaks) {
        if ( leaderboard == null && playerMmr == null && longestStreaks == null ) {
            return null;
        }

        int rank = 0;
        float percentile = 0.0f;
        if ( leaderboard != null ) {
            rank = leaderboard.getRank();
            percentile = leaderboard.getPercentile();
        }
        Instant joinedAt = null;
        OffsetDateTime lastPlayedAt = null;
        int mmr = 0;
        if ( playerMmr != null ) {
            joinedAt = playerMmr.getCreatedAt();
            lastPlayedAt = playerMmr.getLastPlayed();
            mmr = playerMmr.getRankedMmr();
        }
        List<LongestStreakDto> longestStreaks1 = null;
        longestStreaks1 = longestStreakListToLongestStreakDtoList( longestStreaks );

        PlayerStatsDto playerStatsDto = new PlayerStatsDto( rank, mmr, percentile, longestStreaks1, joinedAt, lastPlayedAt );

        return playerStatsDto;
    }

    protected LongestStreakDto longestStreakToLongestStreakDto(LongestStreak longestStreak) {
        if ( longestStreak == null ) {
            return null;
        }

        String score = null;
        int longestStreak1 = 0;

        if ( longestStreak.getScore() != null ) {
            score = longestStreak.getScore().name();
        }
        longestStreak1 = longestStreak.getLongestStreak();

        LongestStreakDto longestStreakDto = new LongestStreakDto( score, longestStreak1 );

        return longestStreakDto;
    }

    protected List<LongestStreakDto> longestStreakListToLongestStreakDtoList(List<LongestStreak> list) {
        if ( list == null ) {
            return null;
        }

        List<LongestStreakDto> list1 = new ArrayList<LongestStreakDto>( list.size() );
        for ( LongestStreak longestStreak : list ) {
            list1.add( longestStreakToLongestStreakDto( longestStreak ) );
        }

        return list1;
    }
}
