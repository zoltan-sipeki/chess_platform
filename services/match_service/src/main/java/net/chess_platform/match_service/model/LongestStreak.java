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
import net.chess_platform.match_service.model.MatchResult.Score;

@Entity
@Immutable
public class LongestStreak {

    @Id
    private UUID userId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private Score score;

    private int longestStreak;

    public UUID getUserId() {
        return userId;
    }

    public Player getPlayer() {
        return player;
    }

    public Score getScore() {
        return score;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

}
