package net.chess_platform.chess_service.ws.message.client;

import net.chess_platform.chess_service.chess.piece.Piece.Type;

public class PromotionPayload implements MatchPayload {

    private Type promotedPiece;

    public PromotionPayload() {}

    public PromotionPayload(Type promotee) {
        this.promotedPiece = promotee;
    }

    public Type getPromotedPiece() {
        return promotedPiece;
    }
}
