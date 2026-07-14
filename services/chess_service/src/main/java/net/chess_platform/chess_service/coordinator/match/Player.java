package net.chess_platform.chess_service.coordinator.match;

import java.util.UUID;

import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class Player {

    private UUID id;

    private Integer mmr;

    private Integer newMmr;

    private float score;

    private Color color;

    public Player(UUID id, Integer mmr) {
        this.id = id;
        this.mmr = mmr;
    }

    public UUID getId() {
        return id;
    }

    public Integer getMmr() {
        return mmr;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
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

}
