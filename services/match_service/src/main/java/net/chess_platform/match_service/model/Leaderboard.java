package net.chess_platform.match_service.model;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
@Immutable
public class Leaderboard {

    @Id
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private MatchUser user;

    private int rankedMmr;

    private int rank;

    private float percentile;

    public UUID getUserId() {
        return userId;
    }

    public MatchUser getUser() {
        return user;
    }

    public int getRankedMmr() {
        return rankedMmr;
    }

    public int getRank() {
        return rank;
    }

    public float getPercentile() {
        return percentile;
    }

}
