package net.chess_platform.chat_service.dto;

import java.util.UUID;

public record UserDto(UUID id, String displayName, String avatar, String presence, String activity) {

}
