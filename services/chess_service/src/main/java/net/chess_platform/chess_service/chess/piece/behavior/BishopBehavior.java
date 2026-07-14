package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Bishop;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;
import net.chess_platform.chess_service.chess.piece.Queen;

public class BishopBehavior implements PieceBehavior {

    @Override
    public boolean canBeAppliedTo(Piece piece) {
        return piece instanceof Bishop || piece instanceof Queen;
    }

    @Override
    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col) {
        var moveList = new ArrayList<Move>();

        for (int i = row + 1, j = col + 1; i < Chessboard.SIZE && j < Chessboard.SIZE; ++i, ++j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int i = row + 1, j = col - 1; i < Chessboard.SIZE && j >= 0; ++i, --j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; --i, --j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int i = row - 1, j = col + 1; i >= 0 && j < Chessboard.SIZE; --i, ++j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        return moveList;
    }
}
