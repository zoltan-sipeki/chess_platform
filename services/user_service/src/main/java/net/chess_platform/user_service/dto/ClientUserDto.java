package net.chess_platform.user_service.dto;

import java.util.UUID;

public record ClientUserDto(UUID id, String displayName, String avatar, String email) {

}
