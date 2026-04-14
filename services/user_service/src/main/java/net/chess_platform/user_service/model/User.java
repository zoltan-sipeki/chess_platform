package net.chess_platform.user_service.model;

import java.util.UUID;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "app_user")
@EntityListeners(AuditingEntityListener.class)
public class User extends AuditedEntity implements Persistable<UUID> {

    @Transient
    private boolean isNew = true;

    @Id
    private UUID id;

    private String username;

    private String displayName;

    private String avatar;

    private String email;

    @Transient
    private String password;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostRemove
    public void setNotNew() {
        isNew = false;
    }

    @PostLoad
    @PostPersist
    public void setNew() {
        isNew = true;
    }

}
