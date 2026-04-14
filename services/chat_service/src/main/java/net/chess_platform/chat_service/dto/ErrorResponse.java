package net.chess_platform.chat_service.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String error,
        OffsetDateTime timestamp,
        String path) {

}
