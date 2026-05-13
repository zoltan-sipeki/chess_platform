package net.chess_platform.match_service.dto;

import java.util.UUID;

public record PlayerDto(
        UUID id,
        String avatar,
        String displayName) {

}
