package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.piece.Piece.Color;
import net.chess_platform.chess_service.chess.piece.Piece.Type;

public record PromotionDetails(
    Color color,
    Type promotedPiece
) {

}
