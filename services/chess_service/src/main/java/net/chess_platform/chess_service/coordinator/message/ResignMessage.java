package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

public class ResignMessage implements IRoutableMessage, IMatchMessage {

    private UUID playerId;

    private long matchId;

    public ResignMessage(UUID playerId, long matchId) {
        this.playerId = playerId;
        this.matchId = matchId;
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
