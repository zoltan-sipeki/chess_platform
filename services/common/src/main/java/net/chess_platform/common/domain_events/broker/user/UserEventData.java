package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

public class UserEventData {

    public static class Builder {

        private UserEventData instance = new UserEventData();

        public Builder(UUID id) {
            instance.id = id;
        }

        public Builder username(String username) {
            instance.username = username;
            return this;
        }

        public Builder displayName(String displayName) {
            instance.displayName = displayName;
            return this;
        }

        public Builder avatar(String avatar) {
            instance.avatar = avatar;
            return this;
        }

        public Builder email(String email) {
            instance.email = email;
            return this;
        }

        public UserEventData build() {
            return instance;
        }
    }

    private UUID id;

    private String username;

    private String displayName;

    private String avatar;

    private String email;

    public UserEventData() {
    }

    public UserEventData(UUID id, String username, String displayName, String avatar, String email) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatar = avatar;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

}
