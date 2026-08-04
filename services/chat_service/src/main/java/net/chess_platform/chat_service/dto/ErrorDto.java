package net.chess_platform.chat_service.dto;

import java.time.Instant;

public record ErrorDto(
        int status,
        String error,
        Instant timestamp,
        String path) {

}
