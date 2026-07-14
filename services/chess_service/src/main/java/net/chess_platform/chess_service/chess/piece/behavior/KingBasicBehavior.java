package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class KingBasicBehavior implements PieceBehavior {

    @Override
    public boolean canBeAppliedTo(Piece piece) {
        return piece instanceof King;
    }

    @Override
    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col) {
        var moveList = new ArrayList<Move>();

        for (int i = row - 1; i <= row + 1; ++i) {
            for (int j = col - 1; j <= col + 1; ++j) {
                if (i == row && j == col) {
                    continue;
                }

                if (i < 0 || i >= Chessboard.SIZE || j < 0 || j >= Chessboard.SIZE || (i == row && j == col)) {
                    continue;
                }

                var move = Move.createBasicMove(board, new Position(row, col), new Position(i, j), piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        return moveList;
    }
}
