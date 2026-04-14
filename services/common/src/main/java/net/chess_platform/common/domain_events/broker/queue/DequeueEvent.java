package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

public record DequeueEvent(UUID userId) {

}
