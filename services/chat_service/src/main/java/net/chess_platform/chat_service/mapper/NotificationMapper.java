package net.chess_platform.chat_service.mapper;

import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.common.dto.chat.NotificationDto;

// @Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface NotificationMapper {

    NotificationDto toDto(Notification notification);
}
