package net.chess_platform.matchmaking_service.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;
import net.chess_platform.matchmaking_service.mmqueue.SearchRange;

@Entity
public class Player extends AuditedEntity implements Persistable<UUID>, Comparable<Player> {

    public static class Update {

        private String displayName;

        private String avatar;

        private int rankedMmr;

        private int unrankedMmr;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getRankedMmr() {
            return rankedMmr;
        }

        public void setRankedMmr(int rankedMmr) {
            this.rankedMmr = rankedMmr;
        }

        public int getUnrankedMmr() {
            return unrankedMmr;
        }

        public void setUnrankedMmr(int unrankedMmr) {
            this.unrankedMmr = unrankedMmr;
        }

    }

    @Transient
    private boolean isNew = true;

    @Id
    private UUID id;

    private String displayName;

    private String avatar;

    private int rankedMmr = 1500;

    private int unrankedMmr = 1500;

    @Transient
    private Instant lastExpandedAt;

    @Transient
    private SearchRange searchRange;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getRankedMmr() {
        return rankedMmr;
    }

    public void setRankedMmr(int rankedMmr) {
        this.rankedMmr = rankedMmr;
    }

    public int getUnrankedMmr() {
        return unrankedMmr;
    }

    public void setUnrankedMmr(int unrankedMmr) {
        this.unrankedMmr = unrankedMmr;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostRemove
    public void setNew() {
        this.isNew = true;
    }

    @PostPersist
    @PostLoad
    @PostUpdate
    public void setNotNew() {
        this.isNew = false;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Instant getLastExpandedAt() {
        return lastExpandedAt;
    }

    public void setLastExpandedAt(Instant lastExpandedAt) {
        this.lastExpandedAt = lastExpandedAt;
    }

    public SearchRange getSearchRange() {
        return searchRange;
    }

    public void setSearchRange(SearchRange searchRange) {
        this.searchRange = searchRange;
    }

    public void expandSearchRange() {
        if (searchRange != null) {
            searchRange.expand();
        }
    }

    @Override
    public int compareTo(Player o) {
        if (this.searchRange == null || o.searchRange == null) {
            throw new IllegalArgumentException("Cannot compare to null");
        }
        return searchRange.compareTo(o.searchRange);
    }

}
