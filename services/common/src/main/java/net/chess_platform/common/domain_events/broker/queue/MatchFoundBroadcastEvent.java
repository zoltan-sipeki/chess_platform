package net.chess_platform.common.domain_events.broker.queue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundBroadcastEvent.MatchFoundBroadCastDto;

public class MatchFoundBroadcastEvent extends DomainEvent<MatchFoundBroadCastDto> {

    public static record MatchFoundBroadCastDto(Map<UUID, String> matchmakingTokens) {
    }

    private List<UUID> recipients;

    public MatchFoundBroadcastEvent(List<UUID> recipients, Map<UUID, String> matchmakingTokens) {
        super(DomainEvent.Category.QUEUE, DomainEvent.Type.MATCH_FOUND_BROADCAST,
                new MatchFoundBroadCastDto(matchmakingTokens));
        this.recipients = recipients;
    }

    public List<UUID> getRecipients() {
        return recipients;
    }

}