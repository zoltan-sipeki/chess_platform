package net.chess_platform.common.domain_events.broker.match;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.match.ReplayReadyEvent.Payload;

public class ReplayReadyEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID replayId) {}

    public ReplayReadyEvent(List<UUID> recipients, Payload data) {
        super(recipients, DomainEvent.Category.MATCH, DomainEvent.Type.REPLAY_READY, data);
    }
}
