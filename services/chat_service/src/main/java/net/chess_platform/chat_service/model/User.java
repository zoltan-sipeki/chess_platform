package net.chess_platform.chat_service.model;

import java.util.UUID;

public class User extends AuditedEntity {

    public enum Presence {
        ONLINE,
        OFFLINE,
        AWAY
    }

    public enum Activity {
        LOOKING_FOR_MATCH,
        IN_MATCH
    }

    public static class Update {

        private String displayName;

        private String avatar;

        private Presence presence;

        private Activity activity;

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

        public Presence getPresence() {
            return presence;
        }

        public void setPresence(Presence presence) {
            this.presence = presence;
        }

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

    }

    private UUID id;

    private String displayName;

    private String avatar;

    private Presence presence;

    private Activity activity;

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

    public Presence getPresence() {
        return presence;
    }

    public void setPresence(Presence presence) {
        this.presence = presence;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}
