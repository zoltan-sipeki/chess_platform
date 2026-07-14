package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

import net.chess_platform.chess_service.chess.piece.Piece.Type;
import net.chess_platform.chess_service.ws.message.client.PromotionPayload;

public class PromotionMessage implements IMatchMessage, IRoutableMessage {

    private UUID playerId;

    private long matchId;

    private Type promotedPiece;

    public PromotionMessage(UUID playerId, long matchId, PromotionPayload payload) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.promotedPiece = payload.getPromotedPiece();
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    public Type getPromotedPiece() {
        return promotedPiece;
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
