package net.chess_platform.common.domain_events.broker.user;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public class UserCreatedEvent extends DomainEvent<UserEventData> {

    public UserCreatedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_CREATED);
    }

    public UserCreatedEvent(UserEventData payload) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_CREATED, payload);
    }

}
