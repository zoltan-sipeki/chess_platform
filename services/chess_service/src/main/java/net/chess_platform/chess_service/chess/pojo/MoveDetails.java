package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.PieceColor;

public record MoveDetails(
        PieceColor color,
        Position from,
        Position to) {

}
