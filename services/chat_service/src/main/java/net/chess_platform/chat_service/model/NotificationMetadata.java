package net.chess_platform.chat_service.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class NotificationMetadata {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID receiver;

    private long sequenceNumber = 0;

    private long lastReadSequenceNumber = 0;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public void setReceiver(UUID receiverId) {
        this.receiver = receiverId;
    }

    public long getLastReadSequenceNumber() {
        return lastReadSequenceNumber;
    }

    public void setLastReadSequenceNumber(long lastReadSequenceNumber) {
        this.lastReadSequenceNumber = lastReadSequenceNumber;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}
