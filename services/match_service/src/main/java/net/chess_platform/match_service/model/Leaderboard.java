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
    private UUID playerId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    private int mmr;

    private int rank;

    private float percentile;

    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public int getMmr() {
        return mmr;
    }

    public int getRank() {
        return rank;
    }

    public float getPercentile() {
        return percentile;
    }

}
