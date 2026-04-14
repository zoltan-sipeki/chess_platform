package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberJoinedEvent.GroupChannelMemberJoinedDto;
import net.chess_platform.common.dto.chat.UserDto;

public class GroupChannelMemberJoinedEvent extends SocialEvent<GroupChannelMemberJoinedDto> {

    public static record GroupChannelMemberJoinedDto(UUID channelId, List<UserDto> joinedMembers) {
    }

    public GroupChannelMemberJoinedEvent(List<UUID> recipients, UUID channelId, List<UserDto> joinedMembers) {
        super(recipients, Type.GROUP_CHANNEL_MEMBER_JOINED, new GroupChannelMemberJoinedDto(channelId, joinedMembers));
    }

}
