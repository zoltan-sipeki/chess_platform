package net.chess_platform.chat_service.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class FriendRequest {

    public static class Update {

        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Id
    private UUID id = UUID.randomUUID();

    @DocumentReference
    private User sender;

    private UUID receiver;

    private Status status = Status.PENDING;

    private UUID notification;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiver) {
        this.receiver = receiver;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public UUID getNotification() {
        return notification;
    }

    public void setNotification(UUID notification) {
        this.notification = notification;
    }

}
