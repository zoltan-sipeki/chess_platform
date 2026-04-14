package net.chess_platform.chess_service.chess.piece;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CastlingMove;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KingBasicBehavior;

public class King extends AbstractPiece {

    private IPieceBehavior kingBasicMoves = new KingBasicBehavior();

    public King(int row, int col, PieceColor color, Chessboard board) {
        super(row, col, color, board);
    }

    @Override
    public List<IMove> getMoves() {
        var board = getBoard();
        var row = getRow();
        var col = getCol();

        var moveList = kingBasicMoves.getMoves(board, getColor(), row, col);
        if (canCastle(Chessboard.LEFT_ROOK_COL)) {
            moveList.add(new CastlingMove(board, new Position(row, col), new Position(row, 2), new Position(row, 0)));
        } else if (canCastle(Chessboard.RIGHT_ROOK_COL)) {
            moveList.add(new CastlingMove(board, new Position(row, col), new Position(row, Chessboard.SIZE - 2),
                    new Position(row, Chessboard.SIZE - 1)));
        }

        return moveList;
    }

    private boolean canCastle(int rookCol) {
        if (hasMoved()) {
            return false;
        }

        int dir = rookCol == Chessboard.LEFT_ROOK_COL ? -1 : 1;

        var board = getBoard();
        var row = getRow();
        var col = getCol();
        var rook = board.getPiece(row, rookCol);

        if (!rook.hasMoved() || !(rook instanceof Rook)) {
            return false;
        }

        for (int j = col - 1; Math.abs(j - rookCol) > 0; j += dir) {
            if (board.getPiece(row, j) != null) {
                return false;
            }
        }

        return true;
    }

    public boolean canCastle() {
        return canCastle(Chessboard.LEFT_ROOK_COL) || canCastle(Chessboard.RIGHT_ROOK_COL);
    }

    @Override
    public String toString() {
        return getColor() == PieceColor.WHITE ? Character.toString(9812) : Character.toString(9818);
    }

}
