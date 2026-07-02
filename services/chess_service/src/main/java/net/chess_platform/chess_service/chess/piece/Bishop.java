package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;

public class Bishop extends AbstractPiece {

    private final IPieceBehavior bishopMoves = new BishopBehavior();

    public Bishop(int row, int col, Color color, Chessboard board) {
        super(row, col, color, board, Type.BISHOP);

    }

    @Override
    public List<IMove> getMoves() {
        return bishopMoves.getMoves(getBoard(), getColor(), getRow(), getCol());
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9815) : Character.toString(9821);
    }
}
