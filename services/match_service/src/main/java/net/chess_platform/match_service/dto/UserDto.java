package net.chess_platform.match_service.dto;

import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String displayName) {

}
