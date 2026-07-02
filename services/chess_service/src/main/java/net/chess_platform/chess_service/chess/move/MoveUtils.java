package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece.Color;
import net.chess_platform.chess_service.chess.piece.King;

public class MoveUtils {

    public static IMove createBasicMove(Chessboard board, Position from, Position to,
            Color color) {
        var target = board.getPiece(to);

        if (target == null) {
            return new SimpleMove(board, from, to);
        }

        if (target.getColor() != color && !(target instanceof King)) {
            return new CaptureMove(board, from, to);
        }

        return null;
    }
}
