package net.chess_platform.matchmaking_api_service.dto;

import java.time.OffsetDateTime;

public record ErrorDto(
        int status,
        String error,
        String path,
        OffsetDateTime timestamp) {

}
