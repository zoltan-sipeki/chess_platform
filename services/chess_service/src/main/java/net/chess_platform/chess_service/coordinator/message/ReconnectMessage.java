package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

public class ReconnectMessage implements IRoutableMessage, IMatchMessage {

    private UUID userId;

    private long matchId;

    public ReconnectMessage(UUID userId, long matchId) {
        this.userId = userId;
        this.matchId = matchId;
    }

    @Override
    public UUID getPlayerId() {
        return userId;
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
