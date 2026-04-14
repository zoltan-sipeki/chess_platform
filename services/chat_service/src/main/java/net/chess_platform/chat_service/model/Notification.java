package net.chess_platform.chat_service.model;

import java.util.UUID;

public class Notification extends AuditedEntity {

    public static class FriendRequestDetails {

        public enum Status {
            PENDING,
            ACCEPTED,
            DECLINED
        }

        private Status status = Status.PENDING;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

    }

    public enum Type {
        FRIEND_REQUEST_ACCEPTED,
        FRIEND_REQUEST
    }

    private UUID id = UUID.randomUUID();

    private Type type;

    private boolean read;

    private UUID senderId;

    private User sender;

    private UUID receiverId;

    private Object details;

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public void setFriendRequestStatus(FriendRequestDetails.Status status) {
        var details = (FriendRequestDetails) this.details;
        details.setStatus(status);
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

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean isRead) {
        this.read = isRead;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

}
