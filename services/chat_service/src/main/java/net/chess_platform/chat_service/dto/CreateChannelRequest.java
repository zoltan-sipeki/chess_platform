package net.chess_platform.chat_service.dto;

import java.util.List;
import java.util.UUID;

import net.chess_platform.chat_service.validator.CreateChannelRequestValidator.CreateChannelRequestConstraint;

@CreateChannelRequestConstraint
public record CreateChannelRequest(
                String type,
                List<UUID> recipients) {
}
