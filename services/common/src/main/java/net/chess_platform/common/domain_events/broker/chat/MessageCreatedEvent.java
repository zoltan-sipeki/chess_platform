package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.chat.MessageDto;

public class MessageCreatedEvent extends SocialEvent<MessageDto> {

	public MessageCreatedEvent(List<UUID> recipients, MessageDto dto) {
		super(recipients, DomainEvent.Type.MESSAGE_CREATED, dto);
	}
}
