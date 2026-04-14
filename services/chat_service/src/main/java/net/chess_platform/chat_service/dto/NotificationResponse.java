package net.chess_platform.chat_service.dto;

import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID senderId,
        UUID receiverId,
        String type,
        boolean isRead,
        Object details) {

}
