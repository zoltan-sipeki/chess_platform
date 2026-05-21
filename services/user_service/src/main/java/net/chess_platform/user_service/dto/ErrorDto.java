package net.chess_platform.user_service.dto;

import java.time.OffsetDateTime;

public record ErrorDto(
        int status,
        String error,
        OffsetDateTime timestamp,
        String path) {

}
