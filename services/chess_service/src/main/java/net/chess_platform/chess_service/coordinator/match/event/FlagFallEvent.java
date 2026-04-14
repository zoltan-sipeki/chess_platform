package net.chess_platform.chess_service.coordinator.match.event;

import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;

public record FlagFallEvent(
                long matchId,
                MoveProcessingResult moveResult) {

}
