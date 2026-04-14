package net.chess_platform.chat_service.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message extends AuditedEntity {

    private UUID id = UUID.randomUUID();

    private UUID channelId;

    private long messageId;

    private String content;

    private UUID senderId;

    private List<User> sender;

    private OffsetDateTime lastEditedAt;

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

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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

    public OffsetDateTime getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(OffsetDateTime lastEditedAt) {
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
