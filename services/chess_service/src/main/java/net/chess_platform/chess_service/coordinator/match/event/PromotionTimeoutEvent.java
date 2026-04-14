package net.chess_platform.chess_service.coordinator.match.event;

import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;

public record PromotionTimeoutEvent(
        long matchId,
        MoveProcessingResult moveResult) {
}