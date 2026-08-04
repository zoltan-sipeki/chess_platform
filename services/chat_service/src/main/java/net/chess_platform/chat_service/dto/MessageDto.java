package net.chess_platform.chat_service.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID channelId,
        UserDto sender,
        long sequenceNumber,
        String content,
        Instant createdAt,
        Instant lastEditedAt) {

}
