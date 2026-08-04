package net.chess_platform.chat_service.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Channel extends AuditedEntity {

    public static class Update {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public enum Type {
        DM,
        GROUP
    }

    private UUID id = UUID.randomUUID();

    private String name;

    private Type type;

    private long nextMessageSeq = 0;

    private Set<UUID> memberIds;

    private Set<User> members;

    public void addMember(UUID userId) {
        if (memberIds == null) {
            memberIds = new HashSet<>();
        }
        memberIds.add(userId);
    }

    public void addMembers(List<UUID> userIds) {
        userIds.forEach(this::addMember);
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

    public long getNextMessageSeq() {
        return nextMessageSeq;
    }

    public void setNextMessageSeq(long nextMessageId) {
        this.nextMessageSeq = nextMessageId;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<UUID> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<UUID> memberIds) {
        this.memberIds = memberIds;
    }

}
