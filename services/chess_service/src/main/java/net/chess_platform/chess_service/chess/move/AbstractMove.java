package net.chess_platform.chess_service.chess.move;

import java.time.OffsetDateTime;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public abstract class AbstractMove implements IMove {

    private Chessboard board;

    private Position from;

    private Position to;

    private AbstractPiece movedPiece;

    private String algebraicNotation;

    private boolean isCheck;

    private long timestamp;

    public AbstractMove(Chessboard board, Position from, Position to) {
        this.board = board;
        this.from = from;
        this.to = to;
        this.movedPiece = board.getPiece(from);
    }

    @Override
    public boolean validate() {
        execute();
        boolean isKingInCheck = board.isKingInCheck(movedPiece.getColor());
        undo();
        return isKingInCheck;
    }

    @Override
    public void execute() {
        movedPiece.incrementMoveCount();
    }

    @Override
    public void undo() {
        movedPiece.decrementMoveCount();
    }

    @Override
    public Chessboard getBoard() {
        return board;
    }

    @Override
    public Position getFrom() {
        return from;
    }

    @Override
    public AbstractPiece getMovedPiece() {
        return movedPiece;
    }

    @Override
    public Position getTo() {
        return to;
    }

    @Override
    public String getAlgebraicNotation() {
        return algebraicNotation;
    }

    @Override
    public void setCheck() {
        isCheck = true;
        algebraicNotation += "+";
    }

    @Override
    public void setCheckmate() {
        isCheck = true;
        algebraicNotation += "++";
    }

    @Override
    public void setAlgebraicNotation(String algebraicNotation) {
        this.algebraicNotation = algebraicNotation;
    }

    @Override
    public void setTimestamp() {
        this.timestamp = OffsetDateTime.now().toInstant().toEpochMilli();
    }

    @Override
    public boolean isCheck() {
        return isCheck;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public AbstractPiece getPromotee() {
        return null;
    }

}
