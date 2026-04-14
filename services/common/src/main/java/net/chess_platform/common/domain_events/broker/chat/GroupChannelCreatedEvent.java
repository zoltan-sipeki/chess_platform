package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.dto.chat.ChannelDto;

public class GroupChannelCreatedEvent extends SocialEvent<ChannelDto> {

    public GroupChannelCreatedEvent(List<UUID> recipients, ChannelDto channel) {
        super(recipients, Type.GROUP_CHANNEL_CREATED, channel);
    }
}
