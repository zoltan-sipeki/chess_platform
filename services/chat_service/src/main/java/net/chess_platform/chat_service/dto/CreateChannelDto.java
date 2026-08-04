package net.chess_platform.chat_service.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import net.chess_platform.chat_service.model.Channel;

public record CreateChannelDto(
        Channel.Type type,
        @NotEmpty List<UUID> members) {
}
