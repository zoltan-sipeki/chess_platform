package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageDeletedEvent.Payload;

public class MessageDeletedEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID messageId) {
    }

    public MessageDeletedEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, DomainEvent.Category.SOCIAL, DomainEvent.Type.MESSAGE_DELETED, data);
    }
}
