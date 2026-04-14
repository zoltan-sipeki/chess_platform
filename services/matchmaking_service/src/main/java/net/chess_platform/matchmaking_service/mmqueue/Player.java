package net.chess_platform.matchmaking_service.mmqueue;

import java.time.LocalDateTime;
import java.util.UUID;

public class Player implements Comparable<Player> {

    private final UUID id;

    private int rankedMmr = -1;

    private int unrankedMmr = -1;

    private SearchRange searchRange;

    private LocalDateTime lastExpanded;

    public Player(UUID userId) {
        this.id = userId;
    }

    public Player(UUID userId, int rankedMmr, int unrankedMmr) {
        this.id = userId;
        this.rankedMmr = rankedMmr;
        this.unrankedMmr = unrankedMmr;
    }
    
    public UUID getId() {
        return id;
    }

    @Override
    public int compareTo(Player o) {
        return searchRange.compareTo(o.searchRange);
    }

    public LocalDateTime getLastExpanded() {
        return lastExpanded;
    }

    public void expandSearchRange() {
        if (searchRange != null) {
            searchRange.expand();
        }
    }

    public int getRankedMmr() {
        return rankedMmr;
    }

    public int getUnrankedMmr() {
        return unrankedMmr;
    }

    public void setSearchRange(SearchRange searchRange) {
        this.searchRange = searchRange;
    }

    public void setLastExpanded(LocalDateTime inQueueSince) {
        this.lastExpanded = inQueueSince;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    
}
