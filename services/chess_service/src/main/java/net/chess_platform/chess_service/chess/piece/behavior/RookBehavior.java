package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;
import net.chess_platform.chess_service.chess.piece.Queen;
import net.chess_platform.chess_service.chess.piece.Rook;

public class RookBehavior implements PieceBehavior {

    @Override
    public boolean canBeAppliedTo(Piece piece) {
        return piece instanceof Rook || piece instanceof Queen;
    }

    @Override
    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col) {
        var moveList = new ArrayList<Move>();

        for (int j = col + 1; j < Chessboard.SIZE; ++j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(row, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }
            
            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int j = col - 1; j >= 0; --j) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(row, j), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int i = row + 1; i < Chessboard.SIZE; ++i) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, col), piece, color);
            if (move != null) {
                moveList.add(move);
            }

            if (move == null || move instanceof CaptureMove) {
                break;
            }
        }

        for (int i = row - 1; i >= 0; --i) {
            var move = Move.createBasicMove(board, new Position(row, col), new Position(i, col), piece, color);
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
