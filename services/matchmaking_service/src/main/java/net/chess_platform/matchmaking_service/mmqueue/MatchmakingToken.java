package net.chess_platform.matchmaking_service.mmqueue;

import java.util.UUID;

public record MatchmakingToken(UUID playerId, String jwt) {

}
