package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberJoinedEvent.Payload;

public class GroupChannelMemberJoinedEvent extends BroadcastEvent<Payload> {

    public static record Payload(UUID channelId, List<UserData> joinedMembers) {
    }

    public GroupChannelMemberJoinedEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.GROUP_CHANNEL_MEMBER_JOINED, data);
    }

}
