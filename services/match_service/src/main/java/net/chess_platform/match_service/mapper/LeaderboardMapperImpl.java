package net.chess_platform.match_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.LeaderboardEntryDto;
import net.chess_platform.match_service.dto.PlayerDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.Player;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-13T22:52:30+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class LeaderboardMapperImpl implements LeaderboardMapper {

    @Override
    public LeaderboardEntryDto toDto(Leaderboard leaderboard) {
        if ( leaderboard == null ) {
            return null;
        }

        int mmr = 0;
        PlayerDto player = null;
        int rank = 0;
        float percentile = 0.0f;

        mmr = leaderboard.getMmr();
        player = playerToPlayerDto( leaderboard.getPlayer() );
        rank = leaderboard.getRank();
        percentile = leaderboard.getPercentile();

        LeaderboardEntryDto leaderboardEntryDto = new LeaderboardEntryDto( player, rank, mmr, percentile );

        return leaderboardEntryDto;
    }

    @Override
    public List<LeaderboardEntryDto> toDtoList(List<Leaderboard> leaderboards) {
        if ( leaderboards == null ) {
            return null;
        }

        List<LeaderboardEntryDto> list = new ArrayList<LeaderboardEntryDto>( leaderboards.size() );
        for ( Leaderboard leaderboard : leaderboards ) {
            list.add( toDto( leaderboard ) );
        }

        return list;
    }

    protected PlayerDto playerToPlayerDto(Player player) {
        if ( player == null ) {
            return null;
        }

        UUID id = null;
        String avatar = null;
        String displayName = null;

        id = player.getId();
        avatar = player.getAvatar();
        displayName = player.getDisplayName();

        PlayerDto playerDto = new PlayerDto( id, avatar, displayName );

        return playerDto;
    }
}
