package net.chess_platform.common.domain_events.broker.chat;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;

public class NotificationEvent extends BroadcastEvent<NotificationEvent.Payload> {

    public static record Payload(UUID id, long seq, String type, UserData sender, UUID friendRequest, Instant createdAt) {

    }

    public NotificationEvent(Collection<UUID> recipients, Payload payload) {
        super(recipients, Category.SOCIAL, DomainEvent.Type.NOTIFICATION, payload);
    }

}
