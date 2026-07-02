package net.chess_platform.common.domain_events.broker.chess;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchStartedEvent.Payload;

public class MatchStartedEvent extends DomainEvent<Payload> {

    public static record Payload(List<UUID> playerIds) {
    }

    public MatchStartedEvent(List<UUID> playerIds) {
        super(DomainEvent.Category.CHESS, DomainEvent.Type.MATCH_STARTED, new Payload(playerIds));
    }

}
