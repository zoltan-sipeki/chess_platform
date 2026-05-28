package net.chess_platform.user_service.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KeycloakUserRepresentation {

    public static class Attributes {

        public List<String> synced = new ArrayList<>();

        public List<String> updated = new ArrayList<>();

        public List<String> getSynced() {
            return synced;
        }

        public List<String> getUpdated() {
            return updated;
        }

    }

    private String id;

    private String username;

    private String email;

    private Attributes attributes = new Attributes();

    private boolean enabled = true;

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSynced(String synced) {
        if (attributes.synced.isEmpty()) {
            attributes.synced.add(synced);
        } else {
            attributes.synced.set(0, synced);
        }
    }

    public void setUpdated(String updated) {
        if (attributes.updated.isEmpty()) {
            attributes.updated.add(updated);
        } else {
            attributes.updated.set(0, updated);
        }
    }
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getSynced() {
        return attributes.synced.isEmpty() ? "" : attributes.synced.get(0);
    }

    @JsonIgnore
    public String getUpdated() {
        return attributes.updated.isEmpty() ? "" : attributes.updated.get(0);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
