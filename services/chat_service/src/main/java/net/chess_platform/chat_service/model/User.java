package net.chess_platform.chat_service.model;

import java.util.UUID;

public class User extends AuditedEntity {

    public static class Update {

        private UUID id;

        private String displayName;

        private String avatar;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
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

    }

    private UUID id;

    private String displayName;

    private String avatar;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String username) {
        this.displayName = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String statusDescription) {
        this.avatar = statusDescription;
    }

}
