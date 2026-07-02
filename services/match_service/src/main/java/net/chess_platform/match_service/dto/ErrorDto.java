package net.chess_platform.match_service.dto;

import java.time.Instant;

public record ErrorDto(
        int status,
        Instant timestamp,
        String error,
        String path) {

}