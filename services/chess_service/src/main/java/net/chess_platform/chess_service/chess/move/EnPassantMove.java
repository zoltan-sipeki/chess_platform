package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public class EnPassantMove extends AbstractMove {

    private AbstractPiece capturedPawn;

    private Position capturedPos;

    public EnPassantMove(Chessboard board, Position from, Position to, Position capturedPos) {
        super(board, from, to);
        this.capturedPos = capturedPos;
        this.capturedPawn = board.getPiece(capturedPos);
        setAlgebraicNotation(getMovedPiece().toString() + getFrom().toString() + "x"
                + getTo().toString() + "e.p.");
    }

    @Override
    public void execute() {
        super.execute();
        var board = getBoard();
        var movedPiece = getMovedPiece();
        var from = getFrom();
        var to = getTo();

        board.setPiece(movedPiece, to);
        board.setPiece(null, from);
        board.setPiece(null, capturedPos);
    }

    @Override
    public void undo() {
        var board = getBoard();
        var movedPiece = getMovedPiece();
        var from = getFrom();
        var to = getTo();

        board.setPiece(movedPiece, from);
        board.setPiece(null, to);
        board.setPiece(capturedPawn, capturedPos);
        super.undo();
    }

}
