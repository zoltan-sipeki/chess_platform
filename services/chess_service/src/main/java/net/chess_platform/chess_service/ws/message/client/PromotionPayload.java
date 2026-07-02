package net.chess_platform.chess_service.ws.message.client;

import net.chess_platform.chess_service.chess.piece.AbstractPiece.Type;

public record PromotionPayload(long matchId, Type promotee) {
}
