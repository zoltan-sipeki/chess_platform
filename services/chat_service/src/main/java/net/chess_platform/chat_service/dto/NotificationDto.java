package net.chess_platform.chat_service.dto;

import java.util.UUID;

import net.chess_platform.common.dto.chat.UserDto;

public record NotificationDto(UUID id, String type, long seq, UserDto sender, UUID friendRequest) {

}
