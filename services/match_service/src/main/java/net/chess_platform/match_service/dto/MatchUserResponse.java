package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchUserResponse(
        UUID id,
        int unrankedMmr,
        int rankedMmr,
        OffsetDateTime lastPlayed) {

}
