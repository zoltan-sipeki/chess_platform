package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberLeftEvent.GroupChannelMemberLeftDto;

public class GroupChannelMemberLeftEvent extends SocialEvent<GroupChannelMemberLeftDto> {

    public static record GroupChannelMemberLeftDto(UUID channelId, UUID leftUserId) {
    }

    public GroupChannelMemberLeftEvent(List<UUID> recipients, UUID channelId, UUID leftUserId) {
        super(recipients, Type.GROUP_CHANNEL_MEMBER_LEFT, new GroupChannelMemberLeftDto(channelId, leftUserId));
    }
}
