package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.chat.MessageDto;

public class MessageDeletedEvent extends SocialEvent<MessageDto> {

    public MessageDeletedEvent(List<UUID> recipients, MessageDto data) {
        super(recipients, DomainEvent.Type.MESSAGE_DELETED, data);
    }
}
