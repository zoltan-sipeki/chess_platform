package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

public record User(UUID id, String displayName, String avatar) {

}
