package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public record MoveDetails(
        Color color,
        Position from,
        Position to) {

}
