package net.chess_platform.common.dto.chat;

import java.util.UUID;

public record NotificationDto(UUID id, String type, UserDto sender, boolean read) {

}
