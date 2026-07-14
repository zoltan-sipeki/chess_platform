package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public interface Move {

    public enum Type {
        SIMPLE,
        EN_PASSANT,
        CASTLING,
        QUEENSIDE_CASTLING,
        KINGSIDE_CASTLING,
        CAPTURE,
    }

    public enum CheckStatus {
        CHECK,
        CHECKMATE
    }

    boolean validate(Chessboard board);

    void execute(Chessboard board);

    void undo(Chessboard board);

    AbstractPiece.Type getPiece();

    Color getColor();

    Position getFrom();

    Position getTo();

    void setCheckStatus(CheckStatus checkStatus);

    boolean isPromotion();

    CheckStatus getCheckStatus();

    long getTimestamp();

    Type getType();

    public static Move createBasicMove(Chessboard board, Position from, Position to,
            AbstractPiece.Type piece, Color color) {
        var target = board.getPiece(to);

        if (target == null) {
            return new SimpleMove(piece, color, from, to);
        }

        if (target.getColor() != color && !(target instanceof King)) {
            return new CaptureMove(piece, color, from, to);
        }

        return null;
    }
}
