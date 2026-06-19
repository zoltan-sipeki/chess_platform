package net.chess_platform.common.domain_events.broker.relay;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent.Payload;

public class PresenceChangedEvent extends BroadcastEvent<Payload> {

    public enum Presence {
        ONLINE,
        AWAY,
        OFFLINE
    }

    public static record Payload(UUID userId, Presence presence) {
    }

    public PresenceChangedEvent(List<UUID> recipients, Payload data) {
        super(recipients, Category.RELAY, Type.PRESENCE_CHANGED, data);
    }
}
