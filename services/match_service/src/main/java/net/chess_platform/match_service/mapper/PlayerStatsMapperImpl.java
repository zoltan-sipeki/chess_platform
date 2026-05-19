package net.chess_platform.match_service.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.match_service.dto.LongestStreakDto;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.LongestStreak;
import net.chess_platform.match_service.model.Player;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T18:30:49+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class PlayerStatsMapperImpl implements PlayerStatsMapper {

    @Override
    public PlayerStatsDto toDto(Leaderboard leaderboard, Player player, List<LongestStreak> longestStreaks) {
        if ( leaderboard == null && player == null && longestStreaks == null ) {
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
        if ( player != null ) {
            joinedAt = player.getCreatedAt();
            lastPlayedAt = player.getLastPlayedAt();
            mmr = player.getRankedMmr();
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

        String outcome = null;
        int longestStreak1 = 0;

        if ( longestStreak.getOutcome() != null ) {
            outcome = longestStreak.getOutcome().name();
        }
        longestStreak1 = longestStreak.getLongestStreak();

        LongestStreakDto longestStreakDto = new LongestStreakDto( outcome, longestStreak1 );

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
