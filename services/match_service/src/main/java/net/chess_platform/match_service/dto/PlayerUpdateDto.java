package net.chess_platform.match_service.dto;

import java.time.Instant;

public record PlayerUpdateDto(
        String displayName,
        String avatar,
        int rankedMmr,
        int unrankedMmr,
        Instant lastPlayedAt

) {

}
