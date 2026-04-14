package net.chess_platform.matchmaking_connection_service.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record PrivateMatchRequest(
        @NotNull UUID inviteeId) {

}
