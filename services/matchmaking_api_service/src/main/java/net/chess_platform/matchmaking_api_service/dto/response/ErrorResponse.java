package net.chess_platform.matchmaking_api_service.dto.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String error,
        String path,
        OffsetDateTime timestamp) {

}
