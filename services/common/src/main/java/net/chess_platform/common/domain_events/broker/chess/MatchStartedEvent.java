package net.chess_platform.common.domain_events.broker.chess;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchStartedEvent.MatchStartedDto;

public class MatchStartedEvent extends DomainEvent<MatchStartedDto> {

    public static record MatchStartedDto(List<UUID> playerIds) {
    }

    public MatchStartedEvent(List<UUID> playerIds) {
        super(DomainEvent.Category.CHESS, DomainEvent.Type.MATCH_STARTED, new MatchStartedDto(playerIds));
    }

}
