package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.Mapping;

import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.dto.chat.MessageDto;
import net.chess_platform.common.dto.chat.UserDto;

// @Mapper(componentModel = "spring")
public interface MessageMapper {

    UserDto toDto(User user);

    @Mapping(target = "sender", expression = "java(toDto(message.getSender()))")
    MessageDto toDto(Message message);

    List<MessageDto> toDtoList(List<Message> messages);

}
