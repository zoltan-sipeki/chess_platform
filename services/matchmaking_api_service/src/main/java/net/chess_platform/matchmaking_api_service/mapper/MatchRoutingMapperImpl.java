package net.chess_platform.matchmaking_api_service.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.matchmaking_api_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_api_service.dto.PlayerDto;
import net.chess_platform.matchmaking_api_service.model.MatchRouting;
import net.chess_platform.matchmaking_api_service.model.Player;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-01T13:43:22+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MatchRoutingMapperImpl implements MatchRoutingMapper {

    @Override
    public CurrentMatchDto toDto(MatchRouting routingData) {
        if ( routingData == null ) {
            return null;
        }

        String status = null;
        UUID target = null;
        PlayerDto inviter = null;
        PlayerDto invitee = null;
        String token = null;

        if ( routingData.getMatchStatus() != null ) {
            status = routingData.getMatchStatus().name();
        }
        target = routingData.getTarget();
        inviter = toDto( routingData.getInviter() );
        invitee = toDto( routingData.getInvitee() );
        token = routingData.getToken();

        CurrentMatchDto currentMatchDto = new CurrentMatchDto( target, inviter, invitee, token, status );

        return currentMatchDto;
    }

    @Override
    public CurrentMatchDto toDtoWithoutToken(MatchRouting routingData) {
        if ( routingData == null ) {
            return null;
        }

        UUID target = null;
        PlayerDto inviter = null;
        PlayerDto invitee = null;

        target = routingData.getTarget();
        inviter = toDto( routingData.getInviter() );
        invitee = toDto( routingData.getInvitee() );

        String token = null;
        String status = null;

        CurrentMatchDto currentMatchDto = new CurrentMatchDto( target, inviter, invitee, token, status );

        return currentMatchDto;
    }

    @Override
    public PlayerDto toDto(Player player) {
        if ( player == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = player.getId();
        displayName = player.getDisplayName();
        avatar = player.getAvatar();

        PlayerDto playerDto = new PlayerDto( id, displayName, avatar );

        return playerDto;
    }
}
