package net.chess_platform.match_service.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MatchDto(UUID id, String type, OffsetDateTime startedAt, OffsetDateTime endedAt, long duration) {

}
