package net.chess_platform.chat_service.dto;

import java.util.List;

public record FriendListDto(
        long total,
        List<UserDto> friends) {

}
