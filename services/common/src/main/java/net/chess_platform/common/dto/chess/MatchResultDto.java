package net.chess_platform.common.dto.chess;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MatchResultDto(long matchId, UUID matchUuid, String matchType, OffsetDateTime startedAt,
        OffsetDateTime endedAt, List<PlayerDto> players, List<MoveDto> replay) {

    public MatchResultDto(long matchId, String matchType, OffsetDateTime startedAt,
            OffsetDateTime endedAt, List<PlayerDto> players, List<MoveDto> replay) {
        this(matchId, UUID.randomUUID(), matchType, startedAt, endedAt, players, replay);
    }

    public static record MoveDto(
            PositionDto from,
            PositionDto to,
            PieceDto movedPiece,
            String type,
            String algebraicNotation,
            boolean isCheck,
            long timestamp,
            String promotee) {
    }

    public static record PieceDto(
            String color,
            String type) {
    }

    public static record PlayerDto(
            UUID id,
            String color,
            Integer mmrBefore,
            Integer mmrAfter,
            String score) {
    }

    public static record PositionDto(
            int row,
            int col) {
    }
}
