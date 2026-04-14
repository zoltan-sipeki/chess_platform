package net.chess_platform.common.domain_events.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import net.chess_platform.common.domain_events.broker.DomainEvent;

@Entity
public class DomainEventData extends AuditedEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DomainEvent.Category category;

    @Enumerated(EnumType.STRING)
    private DomainEvent.Type type;

    private String data;

    @OneToMany(mappedBy = DomainEventAck_.EVENT, fetch = FetchType.EAGER)
    private List<DomainEventAck> acks;

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

    public List<DomainEventAck> getAcks() {
        return acks;
    }

    public void setAcks(List<DomainEventAck> recipients) {
        this.acks = recipients;
    }

    public DomainEvent.Category getCategory() {
        return category;
    }

    public void setCategory(DomainEvent.Category category) {
        this.category = category;
    }
}
