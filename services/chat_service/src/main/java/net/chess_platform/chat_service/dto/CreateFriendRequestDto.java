package net.chess_platform.chat_service.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateFriendRequestDto(
		@NotNull UUID receiverId) {

}
