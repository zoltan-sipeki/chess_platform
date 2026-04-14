package net.chess_platform.chess_service.ws.dto;

public record MoveDto(
        PositionDto from,
        PositionDto to,
        MovedPieceDto movedPiece,
        String type,
        String algebraicNotation,
        boolean isCheck,
        long timestamp,
        String promotee) {

}
