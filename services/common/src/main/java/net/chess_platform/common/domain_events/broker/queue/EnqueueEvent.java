package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

public record EnqueueEvent(UUID userId) {

}
