package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.ChannelTypingEvent.Payload;

public class ChannelTypingEvent
        extends BroadcastEvent<Payload> {

    public static record Payload(UUID userId, UUID channelId) {
    }

    public ChannelTypingEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.CHANNEL_TYPING, data);
    }
}