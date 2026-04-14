package net.chess_platform.chat_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Channel extends AuditedEntity {

    public enum Type {
        DM,
        GROUP
    }

    private UUID id = UUID.randomUUID();

    private String name;

    private Type type;

    private long firstMessageId = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE / 2);

    private long nextMessageId = firstMessageId;

    private List<UUID> memberIds;

    private List<User> members;

    private List<ChannelMember.Role> currentUserRoles;

    public void addMember(UUID userId) {
        if (memberIds == null) {
            memberIds = new ArrayList<>();
        }
        memberIds.add(userId);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public long getNextMessageId() {
        return nextMessageId;
    }

    public void setNextMessageId(long nextMessageId) {
        this.nextMessageId = nextMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<UUID> memberIds) {
        this.memberIds = memberIds;
    }

    public List<ChannelMember.Role> getCurrentUserRoles() {
        return currentUserRoles;
    }

    public void setCurrentUserRoles(List<ChannelMember.Role> roles) {
        this.currentUserRoles = roles;
    }

}
