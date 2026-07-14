package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;

public abstract class AbstractPiece implements Piece {

    private int moveCount = 0;

    private final Color color;

    private final Type type;

    public AbstractPiece(Color color, Type type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return moveCount > 0;
    }

    public void incrementMoveCount() {
        ++moveCount;
    }

    public void decrementMoveCount() {
        --moveCount;
    }

    public abstract List<Move> getMoves(Chessboard board, int row, int col);

    public Move getMove(Position to, Chessboard board, int row, int col) {
        var moves = getMoves(board, row, col);
        for (var move : moves) {
            if (move.getTo().equals(to)) {
                return move;
            }
        }

        return null;
    }

    public boolean canMove(Chessboard board, int row, int col) {
        var moves = getMoves(board, row, col);
        for (var move : moves) {
            if (move.validate(board)) {
                return true;
            }
        }

        return false;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public Type getType() {
        return type;
    }
}
