package net.chess_platform.chat_service.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Message extends AuditedEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID channelId;

    private long sequenceNumber;

    private String content;

    private UUID senderId;

    private List<User> sender;

    private Instant lastEditedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID _id) {
        this.id = _id;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long messageId) {
        this.sequenceNumber = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public Instant getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(Instant lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public User getSender() {
        return sender == null || sender.isEmpty() ? null : sender.get(0);
    }

    public void setSender(List<User> sender) {
        this.sender = sender;
    }

    public void setSender(User sender) {
        if (this.sender == null) {
            this.sender = new ArrayList<>();
        }
        this.sender.add(sender);
    }

}
