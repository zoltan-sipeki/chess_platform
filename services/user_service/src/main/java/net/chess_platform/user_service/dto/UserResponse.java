package net.chess_platform.user_service.dto;

import java.util.UUID;

public record UserResponse(
                UUID id,
                String username,
                String displayName,
                String avatar,
                String email) {

}
