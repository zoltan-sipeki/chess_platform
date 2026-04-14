package net.chess_platform.matchmaking_service.model;

import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;

@Entity
public class MatchmakingUser extends AuditedEntity implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @Id
    private UUID id;

    private int rankedMmr = 1500;

    private int unrankedMmr = 1500;

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

}
