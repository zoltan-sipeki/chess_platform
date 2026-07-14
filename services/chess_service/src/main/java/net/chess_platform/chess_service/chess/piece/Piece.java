package net.chess_platform.chess_service.chess.piece;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;

public interface Piece {

    public enum Type {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }

    public enum Color {
        WHITE,
        BLACK
    }

    Color getColor();

    boolean hasMoved();

    void incrementMoveCount();

    void decrementMoveCount();

    Move getMove(Position to, Chessboard board, int row, int col);

    boolean canMove(Chessboard board, int row, int col);

    int getMoveCount();

    Type getType();

    public static Piece create(Type type, Color color) {
        return switch (type) {
            case PAWN -> new Pawn(color);
            case KNIGHT -> new Knight(color);
            case BISHOP -> new Bishop(color);
            case ROOK -> new Rook(color);
            case QUEEN -> new Queen(color);
            case KING -> new King(color);
        };
    }
}
