package net.chess_platform.match_service.model;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.Transient;

@Entity
public class Match extends AuditedEntity implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @PostLoad
    @PostPersist
    @PostUpdate
    public void setNotNew() {
        isNew = false;
    }

    @PostRemove
    public void setNew() {
        isNew = true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public enum Type {
        RANKED,
        UNRANKED,
        PRIVATE
    }

    @Id
    private UUID id;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    private long duration;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String replay;

    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
    private Set<MatchDetail> matchDetails;

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(OffsetDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public Set<MatchDetail> getMatchDetails() {
        return matchDetails;
    }

    public void setMatchDetails(Set<MatchDetail> matchDetails) {
        this.matchDetails = matchDetails;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAd) {
        this.startedAt = startedAd;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getReplay() {
        return replay;
    }

    public void setReplay(String replay) {
        this.replay = replay;
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
        Match other = (Match) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
