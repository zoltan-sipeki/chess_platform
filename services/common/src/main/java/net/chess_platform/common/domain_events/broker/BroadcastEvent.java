package net.chess_platform.common.domain_events.broker;

import java.util.List;
import java.util.UUID;

public class BroadcastEvent<T> extends DomainEvent<T> {

    private List<UUID> recipients;

    public BroadcastEvent(List<UUID> recipients, Category category, Type type, T data) {
        super(category, type, data);
        this.recipients = recipients;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }
}
