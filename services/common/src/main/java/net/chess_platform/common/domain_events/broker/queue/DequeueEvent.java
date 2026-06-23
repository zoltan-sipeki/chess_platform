package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.ActivityChangedEvent;

public class DequeueEvent extends ActivityChangedEvent {

    protected DequeueEvent() {
    }

    public DequeueEvent(UUID userId) {
        super(Category.QUEUE, new Payload(userId, Activity.LEAVE_QUEUE));
    }
}
