package net.chess_platform.chat_service.model;

import java.util.UUID;

public class User extends AuditedEntity {

    public enum Presence {
        ONLINE,
        OFFLINE,
        AWAY
    }

    public enum Activity {
        LEAVE_QUEUE,
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
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
