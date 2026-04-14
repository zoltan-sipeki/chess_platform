package net.chess_platform.common.domain_events.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DomainEventAck extends AuditedEntity {

    public enum Status {
        PENDING,
        ACKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private DomainEventData event;

    private String serviceName;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private OffsetDateTime lastSentAt = OffsetDateTime.now();

    private OffsetDateTime ackedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String recipient) {
        this.serviceName = recipient;
    }

    public DomainEventData getEvent() {
        return event;
    }

    public void setEvent(DomainEventData event) {
        this.event = event;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OffsetDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(OffsetDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public OffsetDateTime getAckedAt() {
        return ackedAt;
    }

    public void setAckedAt(OffsetDateTime ackedAt) {
        this.ackedAt = ackedAt;
    }

}
