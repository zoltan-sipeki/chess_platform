package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;

public record PlayerUpdateDto(
        String displayName,
        String avatar,
        int rankedMmr,
        int unrankedMmr,
        OffsetDateTime lastPlayedAt

) {

}
