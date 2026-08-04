package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageEditedEvent.Payload;

public class MessageEditedEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID messageId, String content) {
    }

    public MessageEditedEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.MESSAGE_EDITED, data);
    }

}
