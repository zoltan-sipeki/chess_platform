package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CastlingMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.behavior.KingBasicBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;

public class King extends AbstractPiece {

    private final PieceBehavior kingBasicMoves = new KingBasicBehavior();

    public King(Color color) {
        super(color, Type.KING);
    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        var moveList = kingBasicMoves.getMoves(board, getType(), getColor(), row, col);
        if (canCastle(board, row, col, Chessboard.LEFT_ROOK_COL)) {
            moveList.add(new CastlingMove(getType(), getColor(), new Position(row, col), new Position(row, 2)));
        } else if (canCastle(board, row, col, Chessboard.RIGHT_ROOK_COL)) {
            moveList.add(new CastlingMove(getType(), getColor(), new Position(row, col),
                    new Position(row, Chessboard.SIZE - 2)));
        }

        return moveList;
    }

    private boolean canCastle(Chessboard board, int row, int col, int rookCol) {
        if (hasMoved()) {
            return false;
        }

        int dir = rookCol == Chessboard.LEFT_ROOK_COL ? -1 : 1;

        var rook = board.getPiece(row, rookCol);
        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        for (int j = col + dir; Math.abs(j - rookCol) > 0; j += dir) {
            if (board.getPiece(row, j) != null) {
                return false;
            }
        }

        return true;
    }

    public boolean canCastle(Chessboard board, int row, int col) {
        return canCastle(board, row, col, Chessboard.LEFT_ROOK_COL)
                || canCastle(board, row, col, Chessboard.RIGHT_ROOK_COL);
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9812) : Character.toString(9818);
    }

}
