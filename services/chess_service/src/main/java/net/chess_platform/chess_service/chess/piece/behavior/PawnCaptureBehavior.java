package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class PawnCaptureBehavior implements PieceBehavior {

    @Override
    public boolean canBeAppliedTo(Piece piece) {
        return piece instanceof Pawn;
    }

    @Override
    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col) {
        var moveList = new ArrayList<Move>();

        int dir = Chessboard.getPawnDirection(color);
        int targetRow = row + dir;

        if (targetRow < 0 || targetRow >= Chessboard.SIZE) {
            return moveList;
        }

        if (col - 1 >= 0) {
            var target = board.getPiece(targetRow, col - 1);
            if (isValidCapture(target, color)) {
                var move = new CaptureMove(piece, color, new Position(row, col), new Position(targetRow, col - 1));
                moveList.add(move);
            }
        }
        
        if (col + 1 < Chessboard.SIZE) {
            var target = board.getPiece(targetRow, col + 1);
            if (isValidCapture(target, color)) {
                var move = new CaptureMove(piece, color, new Position(row, col), new Position(targetRow, col + 1));
                moveList.add(move);
            }
        }

        return moveList;
    }

    private static boolean isValidCapture(Piece target, Color color) {
        return target != null && target.getColor() != color && !(target instanceof King);
    }
}
