package net.chess_platform.chat_service.dto;

import java.util.List;

import net.chess_platform.common.dto.chat.UserDto;

public record FriendListDto(
        long total,
        List<UserDto> friends) {

}
