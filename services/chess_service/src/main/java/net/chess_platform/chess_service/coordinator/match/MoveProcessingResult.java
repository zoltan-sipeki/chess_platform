package net.chess_platform.chess_service.coordinator.match;

import java.util.List;

import net.chess_platform.chess_service.chess.pojo.MoveResult;

public class MoveProcessingResult {

    private long nextTurn;

    private MoveResult moveResult;

    private List<Player> scoreboard;

    public MoveProcessingResult(MoveResult moveResult, long nextTurn) {
        this.moveResult = moveResult;
        this.nextTurn = nextTurn;
    }

    public MoveProcessingResult(MoveResult moveResult, List<Player> scoreboard) {
        this.moveResult = moveResult;
        this.scoreboard = scoreboard;
    }

    public MoveResult getMoveResult() {
        return moveResult;
    }

    public List<Player> getScoreboard() {
        return scoreboard;
    }

    public long getNextTurn() {
        return nextTurn;
    }

    public boolean isGameOver() {
        return moveResult.isGameOver();
    }
}
