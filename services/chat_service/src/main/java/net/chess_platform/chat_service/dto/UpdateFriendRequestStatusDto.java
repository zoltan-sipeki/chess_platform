package net.chess_platform.chat_service.dto;

import jakarta.validation.constraints.NotNull;
import net.chess_platform.chat_service.model.FriendRequest;

public record UpdateFriendRequestStatusDto(
		@NotNull FriendRequest.Status status) {

}
