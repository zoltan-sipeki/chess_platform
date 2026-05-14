package net.chess_platform.user_service.dto;

import java.util.UUID;

public record ProfileUserDto(UUID id, String displayName, String avatar) {

}
