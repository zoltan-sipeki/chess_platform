package net.chess_platform.common.domain_events.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import net.chess_platform.common.domain_events.broker.DomainEvent;

@Entity
public class AckedDomainEvent extends AuditedEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DomainEvent.Category category;

    @Enumerated(EnumType.STRING)
    private DomainEvent.Type type;

    private String data;

    private OffsetDateTime ackedAt = OffsetDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DomainEvent.Type getType() {
        return type;
    }

    public void setType(DomainEvent.Type type) {
        this.type = type;
    }

    public DomainEvent.Category getCategory() {
        return category;
    }

    public void setCategory(DomainEvent.Category category) {
        this.category = category;
    }

    public OffsetDateTime getAckedAt() {
        return ackedAt;
    }

    public void setAckedAt(OffsetDateTime receivedAt) {
        this.ackedAt = receivedAt;
    }
}
