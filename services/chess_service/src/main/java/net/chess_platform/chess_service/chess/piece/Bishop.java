package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;

public class Bishop extends AbstractPiece {

    private final PieceBehavior bishopMoves = new BishopBehavior();

    public Bishop(Color color) {
        super(color, Type.BISHOP);

    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        return bishopMoves.getMoves(board, getType(), getColor(), row, col);
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9815) : Character.toString(9821);
    }
}
