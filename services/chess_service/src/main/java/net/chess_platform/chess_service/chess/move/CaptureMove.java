package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class CaptureMove extends AbstractMove {

    private Piece capturedPieceInstance;

    public CaptureMove(Piece.Type piece, Color color, Position from, Position to) {
        super(piece, color, from, to, Type.CAPTURE);
    }

    @Override
    public void execute(Chessboard board) {
        super.execute(board);
        var from = getFrom();
        var to = getTo();

        capturedPieceInstance = board.getPiece(to);

        board.setPiece(getMovedPieceInstance(), to);
        board.setPiece(null, from);
    }

    @Override
    public void undo(Chessboard board) {
        var from = getFrom();
        var to = getTo();

        board.setPiece(getMovedPieceInstance(), from);
        board.setPiece(capturedPieceInstance, to);

        capturedPieceInstance = null;

        super.undo(board);
    }
}
