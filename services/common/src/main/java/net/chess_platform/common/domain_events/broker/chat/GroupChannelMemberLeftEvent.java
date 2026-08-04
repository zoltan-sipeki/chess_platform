package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberLeftEvent.Payload;

public class GroupChannelMemberLeftEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID channelId, UUID userId) {
    }

    public GroupChannelMemberLeftEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.GROUP_CHANNEL_MEMBER_LEFT, data);
    }
}
