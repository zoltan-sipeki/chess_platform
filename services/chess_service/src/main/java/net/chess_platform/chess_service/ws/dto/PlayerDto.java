package net.chess_platform.chess_service.ws.dto;

import java.util.UUID;

public record PlayerDto(
        UUID id,
        String color,
        Integer mmrBefore,
        Integer mmrAfter,
        String score) {

}
