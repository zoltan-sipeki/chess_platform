package net.chess_platform.common.dto.chess;

public record MoveDto(
        PositionDto from,
        PositionDto to,
        PieceDto movedPiece,
        String type,
        String algebraicNotation,
        boolean isCheck,
        long timestamp,
        String promotee) {
}