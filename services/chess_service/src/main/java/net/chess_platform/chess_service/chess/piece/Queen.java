package net.chess_platform.chess_service.chess.piece;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.RookBehavior;

public class Queen extends AbstractPiece {

    private PieceBehavior rookMoves = new RookBehavior();

    private PieceBehavior bishopMoves = new BishopBehavior();

    public Queen(Color color) {
        super(color, Type.QUEEN);
    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        var rookMoves_ = rookMoves.getMoves(board, getType(), getColor(), row, col);
        var bishopMoves_ = bishopMoves.getMoves(board, getType(), getColor(), row, col);

        var moveList = new ArrayList<Move>(rookMoves_);
        moveList.addAll(bishopMoves_);

        return moveList;
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9813) : Character.toString(9819);
    }
}
