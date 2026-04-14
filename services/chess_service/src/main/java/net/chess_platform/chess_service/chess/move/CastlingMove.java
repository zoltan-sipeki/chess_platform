package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public class CastlingMove extends AbstractMove {

    private AbstractPiece rook;

    private Position rookPosition;

    private MoveType type;

    public CastlingMove(Chessboard board, Position from, Position to, Position rookPosition) {
        super(board, from, to);
        this.rookPosition = rookPosition;
        this.type = rookPosition.col() == Chessboard.LEFT_ROOK_COL ? MoveType.QUEENSIDE_CASTLING
                : MoveType.KINGSIDE_CASTLING;
        this.rook = board.getPiece(rookPosition);
        setAlgebraicNotation(type == MoveType.QUEENSIDE_CASTLING ? "O-O-O" : "O-O");
    }

    @Override
    public boolean validate() {
        var from = getFrom();
        var to = getTo();
        var board = getBoard();
        var movedPiece = getMovedPiece();

        for (int j = from.col(); j <= to.col(); ++j) {
            if (board.isUnderAttack(to.row(), j, movedPiece.getColor())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void execute() {
        super.execute();
        var from = getFrom();
        var to = getTo();
        var board = getBoard();
        var movedPiece = getMovedPiece();

        board.setPiece(movedPiece, to);
        board.setPiece(null, from);

        if (type == MoveType.QUEENSIDE_CASTLING) {
            board.setPiece(rook, to.row(), to.col() + 1);
        } else {
            board.setPiece(rook, to.row(), to.col() - 1);
        }

        board.setPiece(null, rookPosition);
    }

    @Override
    public void undo() {
        var from = getFrom();
        var to = getTo();
        var board = getBoard();
        var movedPiece = getMovedPiece();

        board.setPiece(null, to);
        board.setPiece(movedPiece, from);

        if (type == MoveType.QUEENSIDE_CASTLING) {
            board.setPiece(null, to.row(), to.col() + 1);
        } else {
            board.setPiece(null, to.row(), to.col() - 1);
        }

        board.setPiece(rook, rookPosition);
        super.undo();
    }

    public MoveType getType() {
        return type;
    }

}
