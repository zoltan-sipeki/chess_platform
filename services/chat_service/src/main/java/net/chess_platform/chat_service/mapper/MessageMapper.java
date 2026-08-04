package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.Mapping;

import net.chess_platform.chat_service.dto.MessageDto;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.UserData;

// @Mapper(componentModel = "spring")
public interface MessageMapper {

    UserData toEventUser(User user);

    @Mapping(target = "sender", expression = "java(toEventUser(message.getSender()))")
    MessageCreatedEvent.Payload toEventPayload(Message message);

    UserDto toDto(User user);

    MessageDto toDto(Message message);

    List<MessageDto> toDtoList(List<Message> messages);

}
