package net.chess_platform.common.domain_events.broker.user;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public class UserUpdatedEvent extends DomainEvent<UserEventData> {

    public UserUpdatedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_UPDATED);
    }

    public UserUpdatedEvent(UserEventData payload) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_UPDATED, payload);
    }

}
