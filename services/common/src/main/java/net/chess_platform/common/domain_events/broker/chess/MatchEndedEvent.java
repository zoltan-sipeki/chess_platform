package net.chess_platform.common.domain_events.broker.chess;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.chess.MatchResultDto;

public class MatchEndedEvent extends DomainEvent<MatchResultDto> {

    public MatchEndedEvent(MatchResultDto match) {
        super(DomainEvent.Category.CHESS, DomainEvent.Type.MATCH_ENDED, match);
    }

}
