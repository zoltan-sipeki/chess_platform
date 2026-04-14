package net.chess_platform.match_service.mapper;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.PlayerMmrDto;
import net.chess_platform.match_service.model.MatchUser;
import net.chess_platform.match_service.model.PlayerMmr;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T22:01:56+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class PlayerMmrMapperImpl implements PlayerMmrMapper {

    @Override
    public PlayerMmrDto toDto(PlayerMmr playerMmr) {
        if ( playerMmr == null ) {
            return null;
        }

        UUID userId = null;
        int mmr = 0;
        OffsetDateTime lastPlayed = null;

        userId = playerMmrUserId( playerMmr );
        mmr = playerMmr.getRankedMmr();
        lastPlayed = playerMmr.getLastPlayed();

        PlayerMmrDto playerMmrDto = new PlayerMmrDto( userId, mmr, lastPlayed );

        return playerMmrDto;
    }

    private UUID playerMmrUserId(PlayerMmr playerMmr) {
        MatchUser user = playerMmr.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
