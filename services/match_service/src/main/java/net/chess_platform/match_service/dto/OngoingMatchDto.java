package net.chess_platform.match_service.dto;

import java.util.UUID;

public record OngoingMatchDto(
    long matchId,
    UUID userId,
    String target
) {

}
