package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;

public abstract class AbstractPiece {

    private int moveCount = 0;

    private int row;

    private int col;

    private PieceColor color;

    private Chessboard board;

    public AbstractPiece(int row, int col, PieceColor color, Chessboard board) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.board = board;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public PieceColor getColor() {
        return color;
    }

    public Chessboard getBoard() {
        return board;
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

    public void setPosition(Position pos) {
        row = pos.row();
        col = pos.col();
    }

    public abstract List<IMove> getMoves();

    public IMove getMove(Position pos) {
        var moves = getMoves();
        for (var move : moves) {
            if (move.getTo().equals(pos)) {
                return move;
            }
        }

        return null;
    }

    public boolean canMove() {
        return !getMoves().isEmpty();
    }

    public int getMoveCount() {
        return moveCount;
    }
}
