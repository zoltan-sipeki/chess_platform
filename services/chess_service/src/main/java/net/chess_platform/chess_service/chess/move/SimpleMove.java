package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;

public class SimpleMove extends AbstractMove {

    public SimpleMove(Chessboard board, Position from, Position to) {
        super(board, from, to);
        setAlgebraicNotation(getMovedPiece().toString() + getFrom().toString() + "-" + getTo().toString());
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
        board.setPiece(null, to);
        super.undo();
    }

}
