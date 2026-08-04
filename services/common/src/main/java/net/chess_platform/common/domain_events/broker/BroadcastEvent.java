package net.chess_platform.common.domain_events.broker;

import java.util.Collection;
import java.util.UUID;

public class BroadcastEvent<T> extends DomainEvent<T> {

    private Collection<UUID> recipients;

    public BroadcastEvent(Collection<UUID> recipients, Category category, Type type, T data) {
        super(category, type, data);
        this.recipients = recipients;
    }

    public Collection<UUID> getRecipients() {
        return recipients;
    }
}
