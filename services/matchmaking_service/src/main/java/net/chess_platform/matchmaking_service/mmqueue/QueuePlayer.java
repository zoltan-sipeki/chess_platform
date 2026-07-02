package net.chess_platform.matchmaking_service.mmqueue;

import java.time.Instant;
import java.util.UUID;

import net.chess_platform.matchmaking_service.model.Player;

public class QueuePlayer implements Comparable<QueuePlayer> {

    private final Player player;

    private SearchRange searchRange;

    private Instant lastExpandedAt;

    public QueuePlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getId() {
        return player.getId();
    }

    public String getDisplayName() {
        return player.getDisplayName();
    }

    public String getAvatar() {
        return player.getAvatar();
    }

    public int getRankedMmr() {
        return player.getRankedMmr();
    }

    public int getUnrankedMmr() {
        return player.getUnrankedMmr();
    }

    public SearchRange getSearchRange() {
        return searchRange;
    }

    public void expandSearchRange() {
        if (searchRange != null) {
            searchRange.expand();
        }
    }

    public void setSearchRange(SearchRange searchRange) {
        this.searchRange = searchRange;
    }

    public Instant getLastExpandedAt() {
        return lastExpandedAt;
    }

    public void setLastExpandedAt(Instant lastExpandedAt) {
        this.lastExpandedAt = lastExpandedAt;
    }

    @Override
    public int compareTo(QueuePlayer o) {
        if (this.searchRange == null || o.searchRange == null) {
            throw new IllegalArgumentException("Cannot compare to null");
        }
        return searchRange.compareTo(o.searchRange);
    }

}
