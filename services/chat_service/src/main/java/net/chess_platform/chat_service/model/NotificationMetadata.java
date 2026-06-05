package net.chess_platform.chat_service.model;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class NotificationMetadata {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID receiver;

    private long sequenceNumber = ThreadLocalRandom.current().nextLong(0, Long.MAX_VALUE / 2);

    private long lastReadSequenceNumber = sequenceNumber;

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
