package net.chess_platform.chess_service.coordinator.match;

import java.util.UUID;

import net.chess_platform.chess_service.chess.piece.PieceColor;

public class Player {

    private UUID id;

    private Integer mmr;

    private Integer newMmr;

    private Float score;

    private PieceColor color;

    private volatile long matchId;

    public Player(UUID id, Integer mmr, long matchId) {
        this.id = id;
        this.mmr = mmr;
        this.matchId = matchId;
    }

    public UUID getId() {
        return id;
    }

    public Integer getMmr() {
        return mmr;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public Integer getNewMmr() {
        return newMmr;
    }

    public Float getScore() {
        return score;
    }

    public void setNewMmr(int mmrAfter) {
        this.newMmr = mmrAfter;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getMatchId() {
        return matchId;
    }

}
