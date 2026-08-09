package net.chess_platform.matchmaking_service.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreatePrivateMatchDto(
        @NotNull UUID inviteeId) {

}
