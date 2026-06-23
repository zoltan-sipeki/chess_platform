package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.ActivityChangedEvent;

public class EnqueueEvent extends ActivityChangedEvent {

    protected EnqueueEvent() {
    }

    public EnqueueEvent(UUID userId) {
        super(Category.QUEUE, new Payload(userId, Activity.LOOKING_FOR_MATCH));
    }
}
