package net.chess_platform.chat_service.dto;

import java.util.List;

public record NotificationListDto(
    long unread,
    long lastReadSeq,
    List<NotificationDto> notifications,
    Long last
) {

}
