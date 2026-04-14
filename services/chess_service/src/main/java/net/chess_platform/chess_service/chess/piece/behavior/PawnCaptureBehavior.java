package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.piece.PieceColor;

public class PawnCaptureBehavior implements IPieceBehavior {

    @Override
    public boolean canBeAppliedTo(AbstractPiece piece) {
        return piece instanceof Pawn;
    }

    @Override
    public List<IMove> getMoves(Chessboard board, PieceColor color, int row, int col) {
        var moveList = new ArrayList<IMove>();

        int dir = Chessboard.getPawnDirection(color);
        int promotionRow = Chessboard.getPromotionRow(color);
        int targetRow = row + dir;

        if (targetRow < 0 || targetRow >= Chessboard.SIZE) {
            return moveList;
        }

        if (col - 1 >= 0) {
            var target = board.getPiece(targetRow, col - 1);
            if (isValidCapture(target, color)) {
                var move = createCaptureMove(board, new Position(row, col), new Position(targetRow, col - 1), promotionRow);
                moveList.add(move);
            }
        } else if (col + 1 < Chessboard.SIZE) {
            var target = board.getPiece(targetRow, col + 1);
            if (isValidCapture(target, color)) {
                var move = createCaptureMove(board, new Position(row, col), new Position(targetRow, col + 1), promotionRow);
                moveList.add(move);
            }
        }

        return moveList;
    }

    private static boolean isValidCapture(AbstractPiece target, PieceColor color) {
        return target != null && target.getColor() != color && !(target instanceof King);
    }

    private static IMove createCaptureMove(Chessboard board, Position from, Position to, int promotionRow) {
        IMove move = new CaptureMove(board, from, to);
        if (to.row() == promotionRow) {
            move = new PromotionMove(move);
        }

        return move;
    }

}
