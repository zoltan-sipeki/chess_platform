package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;

public record ErrorDto(
        int status,
        OffsetDateTime timestamp,
        String error,
        String path) {

}