package net.chess_platform.common.domain_events.broker.chat;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent.Payload;

public class MessageCreatedEvent extends BroadcastEvent<Payload> {

	public static record Payload(UUID id,
			UUID channelId,
			UserData sender,
			long sequenceNumber,
			String content,
			Instant createdAt,
			Instant lastEditedAt) {
	};

	public MessageCreatedEvent(Collection<UUID> recipients, Payload data) {
		super(recipients, DomainEvent.Category.SOCIAL, DomainEvent.Type.MESSAGE_CREATED, data);
	}
}
