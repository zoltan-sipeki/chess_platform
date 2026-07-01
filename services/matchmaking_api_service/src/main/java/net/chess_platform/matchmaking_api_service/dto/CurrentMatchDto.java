package net.chess_platform.matchmaking_api_service.dto;

import java.util.UUID;

public record CurrentMatchDto(UUID target, PlayerDto inviter, PlayerDto invitee, String token, String status) {

}
