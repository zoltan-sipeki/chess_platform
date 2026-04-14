package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public class PromotionMove implements IMove {

    private IMove move;

    private AbstractPiece promotee;

    public PromotionMove(IMove move) {
        this.move = move;
    }

    @Override
    public boolean validate() {
        return move.validate();
    }

    @Override
    public void execute() {
        if (promotee == null) {
            executeFirstHalf();
        } else {
            executeSecondHalf();
        }
    }

    @Override
    public Position getFrom() {
        return move.getFrom();
    }

    @Override
    public AbstractPiece getMovedPiece() {
        return move.getMovedPiece();
    }

    @Override
    public Position getTo() {
        return move.getTo();
    }

    @Override
    public void undo() {
        if (promotee == null) {
            undoFirstHalf();
        } else {
            undoSecondHalf();
        }
    }

    @Override
    public Chessboard getBoard() {
        return move.getBoard();
    }

    private void executeFirstHalf() {
        move.execute();
        getBoard().setPromotionInProgress(true);
    }

    private void executeSecondHalf() {
        var to = getTo();
        var board = getBoard();
        board.setPiece(promotee, to);
        board.setPromotionInProgress(false);
        move.setAlgebraicNotation(move.getAlgebraicNotation() + "=" + promotee.toString());
    }

    private void undoFirstHalf() {
        getBoard().setPromotionInProgress(false);
        move.undo();
    }

    private void undoSecondHalf() {
        var to = getTo();
        var board = getBoard();
        board.setPromotionInProgress(true);
        board.setPiece(getMovedPiece(), to);
    }

    public void setPromotee(AbstractPiece promotee) {
        this.promotee = promotee;
    }

    @Override
    public void setAlgebraicNotation(String algebraicNotation) {
        move.setAlgebraicNotation(algebraicNotation);
    }

    @Override
    public String getAlgebraicNotation() {
        return move.getAlgebraicNotation();
    }

    @Override
    public void setCheck() {
        move.setCheck();
    }

    @Override
    public void setCheckmate() {
        move.setCheckmate();
    }

    @Override
    public void setTimestamp() {
        move.setTimestamp();
    }

    @Override
    public boolean isCheck() {
        return move.isCheck();
    }

    @Override
    public long getTimestamp() {
        return move.getTimestamp();
    }

    @Override
    public AbstractPiece getPromotee() {
        return promotee;
    }

    public IMove getMove() {
        return move;
    }
}
