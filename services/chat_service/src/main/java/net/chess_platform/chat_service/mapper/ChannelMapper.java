package net.chess_platform.chat_service.mapper;

import java.util.List;

import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.common.dto.chat.ChannelDto;

// @Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface ChannelMapper {

    ChannelDto toDto(Channel channel);

    List<ChannelDto> toDtoList(List<Channel> channels);
}
