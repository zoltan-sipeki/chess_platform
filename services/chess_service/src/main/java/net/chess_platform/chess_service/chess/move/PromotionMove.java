package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class PromotionMove implements Move {

    private AbstractMove move;

    private Piece promotedPieceInstance;

    public PromotionMove(AbstractMove move, Piece promotedPieceInstance) {
        this.move = move;
        this.promotedPieceInstance = promotedPieceInstance;
    }

    @Override
    public boolean validate(Chessboard board) {
        return true;
    }

    @Override
    public void execute(Chessboard board) {
        var to = getTo();
        board.setPiece(promotedPieceInstance, to);
    }

    @Override
    public Color getColor() {
        return move.getColor();
    }

    @Override
    public AbstractPiece.Type getPiece() {
        return move.getPiece();
    }

    @Override
    public Position getFrom() {
        return move.getFrom();
    }

    @Override
    public Position getTo() {
        return move.getTo();
    }

    @Override
    public void undo(Chessboard board) {
        var to = getTo();
        board.setPiece(move.getMovedPieceInstance(), to);
    }

    @Override
    public CheckStatus getCheckStatus() {
        return move.getCheckStatus();
    }

    @Override
    public void setCheckStatus(CheckStatus checkStatus) {
        move.setCheckStatus(checkStatus);
    }

    @Override
    public long getTimestamp() {
        return move.getTimestamp();
    }

    @Override
    public boolean isPromotion() {
        return true;
    }

    public Piece getPromotedPieceInstance() {
        return promotedPieceInstance;
    }

    @Override
    public Type getType() {
        return move.getType();
    }
}
