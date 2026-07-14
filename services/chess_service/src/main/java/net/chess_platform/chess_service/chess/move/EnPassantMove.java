package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class EnPassantMove extends AbstractMove {

    private Piece capturedPawnInstance;

    private Position capturedPawnPosition;

    public EnPassantMove(Piece.Type piece, Color color, Position from, Position to) {
        super(piece, color, from, to, Type.EN_PASSANT);
    }

    @Override
    public void execute(Chessboard board) {
        super.execute(board);
        var movedPiece = getMovedPieceInstance();
        var from = getFrom();
        var to = getTo();

        capturedPawnPosition = new Position(to.row() - Chessboard.getPawnDirection(movedPiece.getColor()), to.col());
        capturedPawnInstance = board.getPiece(capturedPawnPosition);

        board.setPiece(movedPiece, to);
        board.setPiece(null, from);
        board.setPiece(null, capturedPawnPosition);
    }

    @Override
    public void undo(Chessboard board) {
        var movedPiece = getMovedPieceInstance();
        var from = getFrom();
        var to = getTo();

        board.setPiece(movedPiece, from);
        board.setPiece(null, to);
        board.setPiece(capturedPawnInstance, capturedPawnPosition);

        capturedPawnPosition = null;
        capturedPawnInstance = null;

        super.undo(board);
    }

}
