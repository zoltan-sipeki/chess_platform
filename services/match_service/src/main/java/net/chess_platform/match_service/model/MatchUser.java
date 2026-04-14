package net.chess_platform.match_service.model;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.Transient;

@Entity
public class MatchUser extends AuditedEntity implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @Id
    private UUID id;

    private String displayName;

    private String avatar;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private PlayerMmr playerMmr;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<PrivacySetting> privacySettings;

    public PlayerMmr getPlayerMmr() {
        return playerMmr;
    }

    public void setPlayerMmr(PlayerMmr playerMmr) {
        this.playerMmr = playerMmr;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    public void setNew() {
        isNew = false;
    }

    @PostRemove
    public void setDeleted() {
        isNew = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        MatchUser other = (MatchUser) obj;
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

    public Set<PrivacySetting> getPrivacySettings() {
        return privacySettings;
    }

    public void setPrivacySettings(Set<PrivacySetting> privacySettings) {
        this.privacySettings = privacySettings;
    }

}
