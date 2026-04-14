package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;

public record Position(
                int row,
                int col) {
        @Override
        public String toString() {
                return (char) ((int) 'a' + col) + "" + (char) (Chessboard.SIZE - row);
        }
}
