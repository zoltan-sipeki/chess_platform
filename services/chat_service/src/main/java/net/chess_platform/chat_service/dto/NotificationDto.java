package net.chess_platform.chat_service.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(UUID id, String type, long seq, UserDto sender, UUID friendRequest,
                Instant createdAt) {
}
