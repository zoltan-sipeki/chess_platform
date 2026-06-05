package net.chess_platform.chat_service.dto;

import java.util.List;

public record NotificationListDto(
    long total,
    long unread,
    List<NotificationDto> notifications
) {

}
