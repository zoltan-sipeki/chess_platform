package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.chat.UnfriendEvent.UnfriendDto;

public class UnfriendEvent extends SocialEvent<UnfriendDto> {

    public static record UnfriendDto(UUID senderId) {
    }

    public UnfriendEvent(List<UUID> recipients, UUID senderId) {
        super(recipients, DomainEvent.Type.UNFRIEND, new UnfriendDto(senderId));
    }

}
