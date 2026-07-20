package net.chess_platform.match_service.dto;

import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchResult;

public record MatchHistorySearchParams(
        Match.Type matchType,
        MatchResult.Outcome outcome) {

}
