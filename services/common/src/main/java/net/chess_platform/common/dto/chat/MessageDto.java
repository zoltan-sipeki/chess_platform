package net.chess_platform.common.dto.chat;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID channelId,
        UserDto sender,
        long messageId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime lastEditedAt) {

}
