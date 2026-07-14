package net.chess_platform.chess_service.chess.move;

import java.time.Instant;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public abstract class AbstractMove implements Move {

    private final Position from;

    private final Position to;

    private final Piece.Type piece;

    private final Color color;

    private final Type type;

    private Piece movedPieceInstance;

    private CheckStatus checkStatus;

    private long timestamp;

    public AbstractMove(Piece.Type piece, Color color, Position from, Position to, Type type) {
        this.piece = piece;
        this.color = color;
        this.from = from;
        this.to = to;
        this.type = type;
    }

    @Override
    public boolean validate(Chessboard board) {
        execute(board);
        boolean isKingInCheck = board.isKingInCheck(movedPieceInstance.getColor());
        undo(board);
        return !isKingInCheck;
    }

    @Override
    public void execute(Chessboard board) {
        timestamp = Instant.now().toEpochMilli();
        movedPieceInstance = board.getPiece(from);
        movedPieceInstance.incrementMoveCount();
    }

    @Override
    public void undo(Chessboard board) {
        movedPieceInstance.decrementMoveCount();
        movedPieceInstance = null;
        timestamp = 0;
    }

    @Override
    public Position getFrom() {
        return from;
    }

    @Override
    public Position getTo() {
        return to;
    }

    @Override
    public CheckStatus getCheckStatus() {
        return checkStatus;
    }

    @Override
    public void setCheckStatus(CheckStatus checkStatus) {
        if (checkStatus == null) {
            return;
        }

        this.checkStatus = checkStatus;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public AbstractPiece.Type getPiece() {
        return piece;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isPromotion() {
        var to = getTo();
        return getPiece() == Piece.Type.PAWN && (to.row() == 7 || to.row() == 0); 
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Piece getMovedPieceInstance() {
        return movedPieceInstance;
    }

}
