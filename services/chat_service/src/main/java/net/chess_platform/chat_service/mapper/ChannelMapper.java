package net.chess_platform.chat_service.mapper;

import java.util.List;

import net.chess_platform.chat_service.dto.ChannelDto;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent;

// @Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ChannelMapper {

    ChannelDto toDto(Channel channel);

    List<ChannelDto> toDtoList(List<Channel> channels);

    GroupChannelCreatedEvent.Payload toEventPayload(Channel channel);
}
