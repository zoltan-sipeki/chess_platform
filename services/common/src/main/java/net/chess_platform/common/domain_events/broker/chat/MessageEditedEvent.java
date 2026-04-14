package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.dto.chat.MessageDto;

public class MessageEditedEvent extends SocialEvent<MessageDto> {

    public MessageEditedEvent(List<UUID> recipients, MessageDto dto) {
        super(recipients, Type.MESSAGE_EDITED, dto);
    }

}
