package net.chess_platform.common.domain_events.broker.relay;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.SocialEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent.PresenceChangedDto;

public class PresenceChangedEvent extends SocialEvent<PresenceChangedDto> {

    public static record PresenceChangedDto(UUID userId, String presence) {
    }

    public PresenceChangedEvent(List<UUID> recipients, UUID userId, String presence) {
        super(recipients, Type.PRESENCE_CHANGED, new PresenceChangedDto(userId, presence));
    }
}
