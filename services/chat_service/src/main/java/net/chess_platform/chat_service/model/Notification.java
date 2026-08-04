package net.chess_platform.chat_service.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class Notification {

    public enum Type {
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST
    }

    @Id
    private UUID id = UUID.randomUUID();

    private long sequenceNumber;

    private Type type;

    @DocumentReference
    private User sender;

    private UUID receiver;

    private Instant createdAt;

    private UUID friendRequest;

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiverId) {
        this.receiver = receiverId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public UUID getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(UUID friendRequest) {
        this.friendRequest = friendRequest;
    }

}
