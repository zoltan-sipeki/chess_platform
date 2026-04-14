package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

import net.chess_platform.chess_service.chess.piece.PieceType;

public class PromotionPayload implements IChessMessage {

    private UUID userId;

    private Long matchId;

    private PieceType promotee;

    public PromotionPayload() {
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    public PieceType getPromotee() {
        return promotee;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
