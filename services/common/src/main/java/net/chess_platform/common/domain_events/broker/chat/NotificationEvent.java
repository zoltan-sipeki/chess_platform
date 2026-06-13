package net.chess_platform.common.domain_events.broker.chat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public class NotificationEvent extends SocialEvent<NotificationEvent.Payload> {

    public static record Payload(UUID id, long seq, String type, User sender, UUID friendRequest, OffsetDateTime createdAt) {

    }

    public NotificationEvent(List<UUID> recipients, Payload payload) {
        super(recipients, DomainEvent.Type.NOTIFICATION, payload);
    }

}
