package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.behavior.KnightBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;

public class Knight extends AbstractPiece {

    private final PieceBehavior knightMoves = new KnightBehavior();

    public Knight(Color color) {
        super(color, Type.KNIGHT);
    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        return knightMoves.getMoves(board, getType(), getColor(), row, col);
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9816) : Character.toString(9822);
    }
}
