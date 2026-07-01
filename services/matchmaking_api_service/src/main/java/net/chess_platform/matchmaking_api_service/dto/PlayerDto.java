package net.chess_platform.matchmaking_api_service.dto;

import java.util.UUID;

public record PlayerDto(UUID id, String displayName, String avatar) {

}
