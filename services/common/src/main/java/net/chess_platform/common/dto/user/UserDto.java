package net.chess_platform.common.dto.user;

import java.util.UUID;

public record UserDto(UUID userId, String username, String displayName, String email, String avatar) {
}
