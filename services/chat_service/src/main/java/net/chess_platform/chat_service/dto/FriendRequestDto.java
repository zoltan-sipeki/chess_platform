package net.chess_platform.chat_service.dto;

import java.util.UUID;

public record FriendRequestDto(UUID id, UserDto sender) {

}
