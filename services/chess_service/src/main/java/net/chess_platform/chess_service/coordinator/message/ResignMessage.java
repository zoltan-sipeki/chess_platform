package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

import net.chess_platform.chess_service.ws.message.client.ResignPayload;

public class ResignMessage implements IRoutableMessage, IMatchMessage {

    private UUID playerId;

    private long matchId;

    public ResignMessage(UUID playerId, ResignPayload payload) {
        this.playerId = playerId;
        this.matchId = payload.matchId();
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    @Override
    public long getRoutingKey() {
        return matchId;
    }

}
