package net.chess_platform.chat_service.dto;

import java.util.Set;
import java.util.UUID;

public record ContactsDto(UUID userId, Set<UUID> contacts) {

}
