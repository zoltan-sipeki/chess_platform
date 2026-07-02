package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.ws.message.client.MovePayload;

public class MoveMessage implements IRoutableMessage, IMatchMessage {

    private UUID userId;

    private long matchId;

    private Position from;

    private Position to;

    public MoveMessage(UUID playerId, MovePayload payload) {
        this.userId = playerId;
        this.matchId = payload.matchId();
        this.from = payload.from();
        this.to = payload.to();
    }

    @Override
    public UUID getPlayerId() {
        return userId;
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    @Override
    public long getRoutingKey() {
        return matchId;
    }
}
