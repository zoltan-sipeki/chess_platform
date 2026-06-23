package net.chess_platform.common.domain_events.broker;

import java.util.UUID;

public class ActivityChangedEvent extends DomainEvent<ActivityChangedEvent.Payload> {

    public enum Activity {
        LOOKING_FOR_MATCH,
        LEAVE_QUEUE,
        IN_MATCH
    }

    protected ActivityChangedEvent() {
    }

    public static record Payload(UUID userId, Activity activity) {
    }

    protected ActivityChangedEvent(Category category, Payload data) {
        super(category, DomainEvent.Type.ACTIVITY_CHANGED, data);
    }
}
