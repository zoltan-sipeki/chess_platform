package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent.Payload;

public class GroupChannelCreatedEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID id, String name, String type, List<UserData> members) {
    }

    public GroupChannelCreatedEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.GROUP_CHANNEL_CREATED, data);
    }
}
