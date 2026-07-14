package net.chess_platform.chess_service.coordinator.match;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class MoveProcessingResult {

    private long nextTurn;

    private Color activeColor;

    private Move move;

    private Chessboard.State state;

    private Color winnerColor;

    private List<Player> scoreboard;

    public MoveProcessingResult(Color activeColor, Move move, Chessboard.State state, long nextTurn) {
        this.activeColor = activeColor;
        this.move = move;
        this.state = state;
        this.nextTurn = nextTurn;
    }

    public MoveProcessingResult(Color activeColor, Move move, Chessboard.State state, Color winnerColor,
            List<Player> scoreboard) {
        this.activeColor = activeColor;
        this.move = move;
        this.state = state;
        this.winnerColor = winnerColor;
        this.scoreboard = scoreboard;
    }

    public Color getWinnerColor() {
        return winnerColor;
    }

    public List<Player> getScoreboard() {
        return scoreboard;
    }

    public long getNextTurn() {
        return nextTurn;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public Move getMove() {
        return move;
    }

    public Chessboard.State getState() {
        return state;
    }

    public boolean isGameOver() {
        return state != Chessboard.State.ACTIVE && state != Chessboard.State.AWAITING_PROMOTION;
    }

    public boolean isInvalid() {
        return move == null && scoreboard == null;
    }

}
