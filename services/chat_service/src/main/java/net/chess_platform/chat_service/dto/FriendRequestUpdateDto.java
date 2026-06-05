package net.chess_platform.chat_service.dto;

import net.chess_platform.chat_service.model.FriendRequest;

public record FriendRequestUpdateDto(
        FriendRequest.Status status) {

}
