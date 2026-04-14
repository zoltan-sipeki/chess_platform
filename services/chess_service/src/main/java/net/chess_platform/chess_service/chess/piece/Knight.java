package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KnightBehavior;

public class Knight extends AbstractPiece {

    private IPieceBehavior knightMoves = new KnightBehavior();

    public Knight(int row, int col, PieceColor color, Chessboard board) {
        super(row, col, color, board);
    }

    @Override
    public List<IMove> getMoves() {
        return knightMoves.getMoves(getBoard(), getColor(), getRow(), getCol());
    }

    @Override
    public String toString() {
        return getColor() == PieceColor.WHITE ? Character.toString(9816) : Character.toString(9822);
    }
}
