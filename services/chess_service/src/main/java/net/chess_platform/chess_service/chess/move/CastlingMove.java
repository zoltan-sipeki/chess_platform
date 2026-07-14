package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class CastlingMove extends AbstractMove {

    private Piece rook;

    public CastlingMove(Piece.Type piece, Color color, Position from, Position to) {
        super(piece, color, from, to, isKingSide(from, to) ? Type.KINGSIDE_CASTLING : Type.QUEENSIDE_CASTLING);
    }

    @Override
    public boolean validate(Chessboard board) {
        var from = getFrom();
        var to = getTo();

        int dir = getType() == Move.Type.KINGSIDE_CASTLING ? 1 : -1;

        var rookPosition = getRookPosition();

        for (int j = from.col(); Math.abs(j - rookPosition.col()) > 0; j += dir) {
            if (board.isUnderAttack(to.row(), j, getColor())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute(Chessboard board) {
        super.execute(board);
        var from = getFrom();
        var to = getTo();
        var movedPiece = getMovedPieceInstance();

        var rookPosition = getRookPosition();

        rook = board.getPiece(rookPosition);

        board.setPiece(movedPiece, to);
        board.setPiece(null, from);

        if (getType() == Move.Type.QUEENSIDE_CASTLING) {
            board.setPiece(rook, to.row(), to.col() + 1);
        } else {
            board.setPiece(rook, to.row(), to.col() - 1);
        }

        board.setPiece(null, rookPosition);
    }

    @Override
    public void undo(Chessboard board) {
        var from = getFrom();
        var to = getTo();
        var movedPiece = getMovedPieceInstance();

        board.setPiece(null, to);
        board.setPiece(movedPiece, from);

        if (getType() == Move.Type.QUEENSIDE_CASTLING) {
            board.setPiece(null, to.row(), to.col() + 1);
        } else {
            board.setPiece(null, to.row(), to.col() - 1);
        }

        board.setPiece(rook, getRookPosition());

        rook = null;

        super.undo(board);
    }

    private Position getRookPosition() {
        return new Position(getTo().row(),
                getType() == Type.QUEENSIDE_CASTLING ? Chessboard.LEFT_ROOK_COL : Chessboard.RIGHT_ROOK_COL);
    }

    private static boolean isKingSide(Position from, Position to) {
        return from.col() < to.col();
    }

}
