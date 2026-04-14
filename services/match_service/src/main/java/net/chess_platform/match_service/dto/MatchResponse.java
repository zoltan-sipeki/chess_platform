package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchResponse(
        UUID id,
        OffsetDateTime createdAt,
        long duration,
        String type,
        UUID userid,
        UUID opponentId,
        String color,
        String score) {

}