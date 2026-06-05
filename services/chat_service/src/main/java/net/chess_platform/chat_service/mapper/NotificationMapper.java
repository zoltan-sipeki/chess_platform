package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.chat_service.dto.NotificationDto;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.common.domain_events.broker.chat.NotificationEvent;

// @Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface NotificationMapper {

    NotificationEvent.Payload toEventPayload(Notification notification);

    List<NotificationDto> toDtoList(List<Notification> list);

    @Mapping(target = "seq", source = "sequenceNumber")
    NotificationDto toDto(Notification notification);
}
