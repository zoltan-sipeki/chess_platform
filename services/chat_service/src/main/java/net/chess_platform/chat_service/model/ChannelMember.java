package net.chess_platform.chat_service.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChannelMember extends AuditedEntity {

    public static class Update {

        private Long lastReadMessageSeq;

        private Long lastReadableMessageSeq;

        private Boolean removed;

        private Set<Role> roles;

        public Long getLastReadMessageSeq() {
            return lastReadMessageSeq;
        }

        public void setLastReadMessageSeq(Long lastReadMessageId) {
            this.lastReadMessageSeq = lastReadMessageId;
        }

        public Long getLastReadableMessageSeq() {
            return lastReadableMessageSeq;
        }

        public void setLastReadableMessageSeq(Long lastReadableMessageId) {
            this.lastReadableMessageSeq = lastReadableMessageId;
        }

        public Boolean getRemoved() {
            return removed;
        }

        public void setRemoved(Boolean removed) {
            this.removed = removed;
        }

        public Set<Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<Role> roles) {
            this.roles = roles;
        }

    }

    public enum Role {
        OWNER,
        MODERATOR,
        MEMBER
    }

    public static class EmbeddedChannel {

        private UUID id;

        private Channel.Type type;

        public EmbeddedChannel(UUID id, Channel.Type type) {
            this.id = id;
            this.type = type;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Channel.Type getType() {
            return type;
        }

        public void setType(Channel.Type type) {
            this.type = type;
        }
    }

    private UUID id = UUID.randomUUID();

    private EmbeddedChannel channel;

    private UUID userId;

    private long lastReadMessageSeq;

    private long lastReadableMessageSeq;

    private Boolean removed;

    private Set<Role> roles;

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }

        roles.add(role);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getLastReadMessageSeq() {
        return lastReadMessageSeq;
    }

    public void setLastReadMessageSeq(long lastReadMessageId) {
        this.lastReadMessageSeq = lastReadMessageId;
    }

    public long getLastReadableMessageSeq() {
        return lastReadableMessageSeq;
    }

    public void setLastReadableMessageSeq(long lastReadableMessageId) {
        this.lastReadableMessageSeq = lastReadableMessageId;
    }

    public EmbeddedChannel getChannel() {
        return channel;
    }

    public void setChannel(EmbeddedChannel channel) {
        this.channel = channel;
    }

    public Boolean isDeleted() {
        return removed;
    }

    public void setRemoved(Boolean isDeleted) {
        this.removed = isDeleted;
    }

    public Boolean isRemoved() {
        return removed;
    }

    public Boolean getRemoved() {
        return removed;
    }

}
