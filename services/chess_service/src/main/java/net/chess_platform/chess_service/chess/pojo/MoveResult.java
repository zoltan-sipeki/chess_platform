package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.Chessboard.GameOverReason;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece.Color;

public class MoveResult {

    private Color activeColor;

    private IMove move;

    private boolean promotionInProgress;

    private Chessboard.GameOverReason gameOverReason;

    private Color winnerColor;

    public MoveResult(IMove move, Color activeColor) {
        this.move = move;
        this.activeColor = activeColor;
    }

    public MoveResult(IMove move, Color activeColor, boolean promotionInProgress) {
        this(move, activeColor);
        this.promotionInProgress = promotionInProgress;
    }

    public MoveResult(IMove move, Chessboard.GameOverReason gameOverReason, Color winnerColor) {
        this(move, null);
        this.gameOverReason = gameOverReason;
        this.winnerColor = winnerColor;
    }

    public boolean isGameOver() {
        return gameOverReason != null;
    }

    public IMove getMove() {
        return move;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public boolean isDraw() {
        return winnerColor == null;
    }

    public boolean isPromotionInProgress() {
        return promotionInProgress;
    }

    public boolean isInvalid() {
        return move == null;
    }

    public Color getWinnerColor() {
        return winnerColor;
    }

    public GameOverReason getGameOverReason() {
        return gameOverReason;
    }

}
