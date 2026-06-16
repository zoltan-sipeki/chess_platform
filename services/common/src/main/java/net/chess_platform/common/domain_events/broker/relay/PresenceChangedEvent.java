package net.chess_platform.common.domain_events.broker.relay;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.SocialEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent.Payload;

public class PresenceChangedEvent extends SocialEvent<Payload> {

    public static record Payload(UUID userId, String presence) {
    }

    public PresenceChangedEvent(List<UUID> recipients, Payload payload) {
        super(recipients, Type.PRESENCE_CHANGED, payload);
    }
}
