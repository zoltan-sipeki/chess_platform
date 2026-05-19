package net.chess_platform.match_service.model;

import java.util.UUID;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import net.chess_platform.match_service.model.MatchResult.Outcome;

@Entity
@Immutable
public class LongestStreak {

    @Id
    private UUID playerId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private Outcome outcome;

    private int longestStreak;

    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

}
