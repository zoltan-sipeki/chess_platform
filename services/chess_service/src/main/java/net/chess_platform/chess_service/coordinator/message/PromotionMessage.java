package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

import net.chess_platform.chess_service.chess.piece.PieceType;
import net.chess_platform.chess_service.ws.message.client.PromotionPayload;

public class PromotionMessage implements IMatchMessage, IRoutableMessage {

    private UUID playerId;

    private long matchId;

    private PieceType promotee;

    public PromotionMessage(UUID playerId, PromotionPayload payload) {
        this.playerId = playerId;
        this.matchId = payload.matchId();
        this.promotee = payload.promotee();
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    public PieceType getPromotee() {
        return promotee;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public long getRoutingKey() {
        return matchId;
    }
}
