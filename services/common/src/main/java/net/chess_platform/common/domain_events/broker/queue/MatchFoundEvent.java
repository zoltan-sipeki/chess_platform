package net.chess_platform.common.domain_events.broker.queue;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent.MatchFoundDto;

public class MatchFoundEvent extends DomainEvent<MatchFoundDto> {

    public static record MatchFoundDto(String matchmakingToken) {
    }

    private UUID recipient;

    public MatchFoundEvent(UUID recipient, String matchmakingToken) {
        super(DomainEvent.Category.QUEUE, DomainEvent.Type.MATCH_FOUND, new MatchFoundDto(matchmakingToken));
        this.recipient = recipient;
    }

    public UUID getRecipient() {
        return recipient;
    }

}
