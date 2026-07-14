package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class SimpleMove extends AbstractMove {

    public SimpleMove(AbstractPiece.Type piece, Color color, Position from, Position to) {
        super(piece, color, from, to, Type.SIMPLE);
    }

    @Override
    public void execute(Chessboard board) {
        super.execute(board);
        var from = getFrom();
        var to = getTo();

        board.setPiece(getMovedPieceInstance(), to);
        board.setPiece(null, from);
    }

    @Override
    public void undo(Chessboard board) {
        var from = getFrom();
        var to = getTo();

        board.setPiece(getMovedPieceInstance(), from);
        board.setPiece(null, to);
        super.undo(board);
    }

}
