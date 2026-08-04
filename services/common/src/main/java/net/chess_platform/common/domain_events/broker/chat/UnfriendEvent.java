package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chat.UnfriendEvent.Payload;

public class UnfriendEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID senderId) {
    }

    public UnfriendEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, DomainEvent.Type.UNFRIEND, data);
    }

}
