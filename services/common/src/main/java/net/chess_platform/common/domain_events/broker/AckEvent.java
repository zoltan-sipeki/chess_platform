package net.chess_platform.common.domain_events.broker;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AckEvent {

    private UUID ackedId;

    private DomainEvent.Category ackedCategory;

    private DomainEvent.Type ackedType;

    private String serviceName;

    public AckEvent() {
    }

    @JsonIgnore
    public AckEvent(DomainEvent<?> e, String serviceName) {
        this.ackedId = e.getId();
        this.ackedCategory = e.getCategory();
        this.ackedType = e.getType();
        this.serviceName = serviceName;
    }

    public UUID getAckedId() {
        return ackedId;
    }

    public DomainEvent.Category getAckedCategory() {
        return ackedCategory;
    }

    public DomainEvent.Type getAckedType() {
        return ackedType;
    }

    public String getServiceName() {
        return serviceName;
    }

}