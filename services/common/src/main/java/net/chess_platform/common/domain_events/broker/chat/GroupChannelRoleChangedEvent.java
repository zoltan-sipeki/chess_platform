package net.chess_platform.common.domain_events.broker.chat;

import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelRoleChangedEvent.Payload;

public class GroupChannelRoleChangedEvent extends BroadcastEvent<Payload> {

    public enum Role {
        OWNER,
        MEMBER,
        MODERATOR
    }

    public enum Action {
        ADD,
        DELETE
    }

    public static record Payload(UUID channelId, Role role, Action action) {
    }

    public GroupChannelRoleChangedEvent(Collection<UUID> recipients, Payload data) {
        super(recipients, Category.SOCIAL, Type.GROUP_CHANNEL_ROLE_CHANGED, data);
    }
}
