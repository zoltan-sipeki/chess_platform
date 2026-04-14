package net.chess_platform.match_service.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class PlayerMmr extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private MatchUser user;

    private int unrankedMmr = 1500;

    private int rankedMmr = 1500;

    private OffsetDateTime lastPlayed;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getUnrankedMmr() {
        return unrankedMmr;
    }

    public void setUnrankedMmr(int unrankedMmr) {
        this.unrankedMmr = unrankedMmr;
    }

    public int getRankedMmr() {
        return rankedMmr;
    }

    public void setRankedMmr(int rankedMmr) {
        this.rankedMmr = rankedMmr;
    }

    public OffsetDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(OffsetDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public MatchUser getUser() {
        return user;
    }

    public void setUser(MatchUser user) {
        this.user = user;
    }

}
