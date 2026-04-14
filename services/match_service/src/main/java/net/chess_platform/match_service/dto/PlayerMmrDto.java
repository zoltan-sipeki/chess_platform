package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PlayerMmrDto(
        UUID userId,
        int mmr,
        OffsetDateTime lastPlayed) {

}
