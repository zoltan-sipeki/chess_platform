package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.RookBehavior;

public class Rook extends AbstractPiece {

    private PieceBehavior rookMoves = new RookBehavior();

    public Rook(Color color) {
        super(color, Type.ROOK);
    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        return rookMoves.getMoves(board, getType(), getColor(), row, col);
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9814) : Character.toString(9820);
    }

}
