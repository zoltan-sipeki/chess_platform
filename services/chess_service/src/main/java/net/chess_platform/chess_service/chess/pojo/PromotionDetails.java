package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.piece.PieceType;

public record PromotionDetails(
    PieceColor color,
    PieceType promotee
) {

}
