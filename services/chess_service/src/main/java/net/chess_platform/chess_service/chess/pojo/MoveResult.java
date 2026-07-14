package net.chess_platform.chess_service.chess.pojo;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.Chessboard.State;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class MoveResult {

    private Color activeColor;

    private Move move;

    private Chessboard.State state = State.ACTIVE;

    private Color winnerColor;

    public MoveResult(Move move, Color activeColor, Chessboard.State state) {
        this.move = move;
        this.activeColor = activeColor;
        this.state = state;
    }

    public MoveResult(Move move, Chessboard.State state, Color winnerColor) {
        this.move = move;
        this.state = state;
        this.winnerColor = winnerColor;
    }

    public boolean isGameOver() {
        return state != State.ACTIVE && state != State.AWAITING_PROMOTION;
    }

    public boolean isPromotionInProgress() {
        return state == State.AWAITING_PROMOTION;
    }

    public Move getMove() {
        return move;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public boolean isDraw() {
        return winnerColor == null;
    }

    public boolean isInvalid() {
        return move == null && winnerColor == null;
    }

    public Color getWinnerColor() {
        return winnerColor;
    }

    public State getState() {
        return state;
    }

}
