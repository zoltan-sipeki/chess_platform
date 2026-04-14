package net.chess_platform.relay_service.ws.message;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public record EventPayload(DomainEvent.Type type, Object data) {

}
