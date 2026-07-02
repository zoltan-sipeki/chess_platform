package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.piece.AbstractPiece.Color;
import net.chess_platform.chess_service.chess.piece.AbstractPiece.Type;

public record PromotionDetails(
    Color color,
    Type promotee
) {

}
