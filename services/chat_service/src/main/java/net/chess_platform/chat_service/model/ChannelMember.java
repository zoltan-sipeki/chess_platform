package net.chess_platform.chat_service.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChannelMember extends AuditedEntity {

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

    private List<User> user;

    private long lastReadMessageId;

    private long lastReadableMessageId;

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

    public long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public long getLastReadableMessageId() {
        return lastReadableMessageId;
    }

    public void setLastReadableMessageId(long lastReadableMessageId) {
        this.lastReadableMessageId = lastReadableMessageId;
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

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public Boolean getRemoved() {
        return removed;
    }

}
