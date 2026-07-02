package net.chess_platform.chess_service.ws.message.client;

import net.chess_platform.chess_service.chess.piece.PieceType;

public record PromotionPayload(long matchId, PieceType promotee) {
}
