package net.chess_platform.common.dto.chat;

import java.util.UUID;

public record UserDto(UUID id, String displayName, String avatar, String presence) {

}
