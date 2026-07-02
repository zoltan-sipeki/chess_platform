package net.chess_platform.match_service.mapper;

import org.mapstruct.Mapper;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.user.UserEventData;
import net.chess_platform.match_service.model.Player;

// @Mapper(componentModel = "spring")
public interface PlayerMapper {

    Player.Update toUpdate(UserEventData dto);

    Player.Update toUpdate(MatchEndedEvent.Payload.Player dto);
}
