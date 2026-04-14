package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.Chessboard.GameOverReason;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveType;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.piece.PieceType;
import net.chess_platform.chess_service.coordinator.Mapper;

public class MoveResult {

    private String algebraicNotation;

    private PieceColor activeColor;

    private PieceColor color;

    private MoveType move = MoveType.INVALID;

    private Position from;

    private Position to;

    private boolean promotionInProgress;

    private PieceType promotee;

    private Chessboard.GameOverReason gameOverReason;

    private PieceColor winnerColor;

    public MoveResult(PieceColor activeColor) {
        this.activeColor = activeColor;
    }

    public MoveResult(IMove move, PieceColor activeColor, Chessboard.GameOverReason gameOverReason,
            PieceColor winnerColor,
            boolean promotionInProgress) {
        this(move, activeColor, gameOverReason, winnerColor);
        this.promotionInProgress = promotionInProgress;
    }

    public MoveResult(IMove move, PieceColor activeColor, Chessboard.GameOverReason gameOverReason,
            PieceColor winnerColor) {
        this.activeColor = activeColor;
        if (move != null) {
            this.color = move.getMovedPiece().getColor();
            this.move = Mapper.toMoveType(move);
            this.promotee = Mapper.toPieceType(move.getPromotee());
            this.from = move.getFrom();
            this.to = move.getTo();
            this.algebraicNotation = move.getAlgebraicNotation();
        }
        this.gameOverReason = gameOverReason;
        this.winnerColor = winnerColor;
    }

    public boolean isGameOver() {
        return gameOverReason != null;
    }

    public String getAlgebraicNotation() {
        return algebraicNotation;
    }

    public PieceColor getColor() {
        return color;
    }

    public PieceType getPromotee() {
        return promotee;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public MoveType getMove() {
        return move;
    }

    public PieceColor getActiveColor() {
        return activeColor;
    }

    public boolean isDraw() {
        return winnerColor == PieceColor.NONE;
    }

    public boolean isPromotionInProgress() {
        return promotionInProgress;
    }

    public boolean isInvalid() {
        return move == MoveType.INVALID;
    }

    public PieceColor getWinnerColor() {
        return winnerColor;
    }

    public GameOverReason getGameOverReason() {
        return gameOverReason;
    }

}
