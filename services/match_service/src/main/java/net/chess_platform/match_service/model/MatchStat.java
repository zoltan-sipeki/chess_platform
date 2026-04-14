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
public class MatchStat extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchUser user;

    @Enumerated(EnumType.STRING)
    private Match.Type matchType;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int draws;

    private float winRatio;

    public float getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(float winRatio) {
        this.winRatio = winRatio;
    }

    public MatchUser getUser() {
        return user;
    }

    public void setUser(MatchUser user) {
        this.user = user;
    }

    public Match.Type getMatchType() {
        return matchType;
    }

    public void setMatchType(Match.Type matchType) {
        this.matchType = matchType;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
