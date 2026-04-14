package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public class CaptureMove extends AbstractMove {

    private AbstractPiece capturedPiece;

    public CaptureMove(Chessboard board, Position from, Position to) {
        super(board, from, to);
        this.capturedPiece = board.getPiece(to);
        setAlgebraicNotation(
                getMovedPiece().toString() + getFrom().toString() + "x" + getTo().toString());
    }

    @Override
    public void execute() {
        super.execute();
        var board = getBoard();
        var from = getFrom();
        var to = getTo();

        board.setPiece(getMovedPiece(), to);
        board.setPiece(null, from);
    }

    @Override
    public void undo() {
        var board = getBoard();
        var from = getFrom();
        var to = getTo();

        board.setPiece(getMovedPiece(), from);
        board.setPiece(capturedPiece, to);
        super.undo();
    }

    public AbstractPiece getCapturedPiece() {
        return capturedPiece;
    }
}
