package net.chess_platform.common.domain_events.broker.message.queue.frontend;

import java.util.UUID;

public record DeclineMatchMessage(UUID userId) {

}
