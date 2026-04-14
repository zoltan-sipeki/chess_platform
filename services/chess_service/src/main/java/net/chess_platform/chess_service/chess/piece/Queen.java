package net.chess_platform.chess_service.chess.piece;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.RookBehavior;

public class Queen extends AbstractPiece {

    private IPieceBehavior rookMoves = new RookBehavior();

    private IPieceBehavior bishopMoves = new BishopBehavior();

    public Queen(int row, int col, PieceColor color, Chessboard board) {
        super(row, col, color, board);
    }

    @Override
    public List<IMove> getMoves() {
        var rookMoves_ = rookMoves.getMoves(getBoard(), getColor(), getRow(), getCol());
        var bishopMoves_ = bishopMoves.getMoves(getBoard(), getColor(), getRow(), getCol());

        var moveList = new ArrayList<IMove>(rookMoves_);
        moveList.addAll(bishopMoves_);

        return moveList;
    }

    @Override
    public String toString() {
        return getColor() == PieceColor.WHITE ? Character.toString(9813) : Character.toString(9819);
    }
}
