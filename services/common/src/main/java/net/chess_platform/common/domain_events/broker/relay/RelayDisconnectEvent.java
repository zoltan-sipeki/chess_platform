package net.chess_platform.common.domain_events.broker.relay;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public class RelayDisconnectEvent extends DomainEvent<RelayDisconnectEvent.Payload> {

    public static record Payload(UUID userId) {}

    protected RelayDisconnectEvent() {}

    public RelayDisconnectEvent(UUID userId) {
        super(Category.RELAY, Type.RELAY_DISCONNECT, new Payload(userId));
    }
}
