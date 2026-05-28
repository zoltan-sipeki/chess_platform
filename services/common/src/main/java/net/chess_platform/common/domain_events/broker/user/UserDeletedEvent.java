package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.user.UserDeletedEvent.Payload;

public class UserDeletedEvent extends DomainEvent<Payload> {

    public static record Payload(UUID userId) {
    }

    public UserDeletedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_DELETED);
    }

    public UserDeletedEvent(Payload payload) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_DELETED, payload);
    }

}
