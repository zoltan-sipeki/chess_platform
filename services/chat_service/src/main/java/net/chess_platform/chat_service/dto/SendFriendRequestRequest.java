package net.chess_platform.chat_service.dto;

import java.util.UUID;

public record SendFriendRequestRequest(
        UUID receiverId) {

}
