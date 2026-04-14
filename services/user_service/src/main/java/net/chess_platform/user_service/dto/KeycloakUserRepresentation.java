package net.chess_platform.user_service.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KeycloakUserRepresentation {

    public static class Attributes {

        public List<String> displayName = new ArrayList<>();

        public List<String> avatar = new ArrayList<>();

        public List<String> synced = new ArrayList<>();

        public List<String> getDisplayName() {
            return displayName;
        }

        public List<String> getAvatar() {
            return avatar;
        }

        public List<String> getSynced() {
            return synced;
        }

    }

    // public static class Credential {

    // private String type = "password";

    // public String value;

    // public String getType() {
    // return type;
    // }

    // public String getValue() {
    // return value;
    // }

    // }

    private String id;

    private String username;

    private String email;

    private Attributes attributes = new Attributes();

    // private List<Credential> credentials = new ArrayList<>();

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

    public void setDisplayName(String displayName) {
        if (attributes.displayName.isEmpty()) {
            attributes.displayName.add(displayName);
        } else {
            attributes.displayName.set(0, displayName);
        }
    }

    public void setSynced(String synced) {
        if (attributes.synced.isEmpty()) {
            attributes.synced.add(synced);
        } else {
            attributes.synced.set(0, synced);
        }
    }

    public void setAvatar(String avatar) {
        if (attributes.avatar.isEmpty()) {
            attributes.avatar.add(avatar);
        } else {
            attributes.avatar.set(0, avatar);
        }
    }

    // public void setPassword(String password) {
    // var credential = new Credential();
    // credential.value = password;
    // credentials.add(credential);
    // }

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
    public String getDisplayName() {
        return attributes.displayName.isEmpty() ? "" : attributes.displayName.get(0);
    }

    @JsonIgnore
    public String getAvatar() {
        return attributes.avatar.isEmpty() ? "" : attributes.avatar.get(0);
    }

    @JsonIgnore
    public String getSynced() {
        return attributes.synced.isEmpty() ? "" : attributes.synced.get(0);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    // public List<Credential> getCredentials() {
    // return credentials;
    // }

    public boolean isEnabled() {
        return enabled;
    }
}
