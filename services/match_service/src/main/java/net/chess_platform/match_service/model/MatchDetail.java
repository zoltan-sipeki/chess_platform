package net.chess_platform.match_service.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MatchDetail extends AuditedEntity {

    public enum Score {
        DRAW,
        WIN,
        LOSS
    }

    public enum Color {
        WHITE,
        BLACK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchUser user;

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Score score;

    private Integer mmrBefore;

    private Integer mmrAfter;

    private Integer mmrChange;

    public Integer getMmrChange() {
        return mmrChange;
    }

    public void setMmrChange(Integer mmrChange) {
        this.mmrChange = mmrChange;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public MatchUser getUser() {
        return user;
    }

    public void setUser(MatchUser user) {
        this.user = user;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Integer getMmrBefore() {
        return mmrBefore;
    }

    public void setMmrBefore(Integer mmrBefore) {
        this.mmrBefore = mmrBefore;
    }

    public int getMmrAfter() {
        return mmrAfter;
    }

    public void setMmrAfter(Integer mmrAfter) {
        this.mmrAfter = mmrAfter;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
