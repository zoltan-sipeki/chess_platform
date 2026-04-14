package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.GroupChannelRoleChangedEvent.GroupChannelRoleChangedDto;

public class GroupChannelRoleChangedEvent extends SocialEvent<GroupChannelRoleChangedDto> {

    public enum Role {
        OWNER,
        MEMBER,
        MODERATOR
    }

    public enum Action {
        ADD,
        DELETE
    }

    public static record GroupChannelRoleChangedDto(UUID channelId, Role role, Action action) {
    }

    public GroupChannelRoleChangedEvent(List<UUID> recipients, UUID channelId, Role role, Action action) {
        super(recipients, Type.GROUP_CHANNEL_ROLE_CHANGED, new GroupChannelRoleChangedDto(channelId, role, action));
    }
}
