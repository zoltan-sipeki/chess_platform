package net.chess_platform.relay_service.model;

import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.Transient;

@Entity
public class RelayUser extends AuditedEntity implements Persistable<UUID> {

    public enum Presence {
        ONLINE,
        OFFLINE,
        AWAY
    }

    @Transient
    private boolean isNew = true;

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Presence preferredPresence = Presence.ONLINE;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Presence getPreferredPresence() {
        return preferredPresence;
    }

    public void setPreferredPresence(Presence preferredStatus) {
        this.preferredPresence = preferredStatus;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    public void setNotNew() {
        isNew = false;
    }

    @PostRemove
    public void setNew() {
        isNew = true;
    }
}
