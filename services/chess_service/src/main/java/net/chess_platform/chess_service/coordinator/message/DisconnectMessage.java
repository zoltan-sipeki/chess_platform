package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

public class DisconnectMessage implements IRoutableMessage {

    private UUID playerId;

    private long matchId;

    public DisconnectMessage(UUID userId, long matchId) {
        this.playerId = userId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getMatchId() {
        return matchId;
    }

    @Override
    public long getRoutingKey() {
        return matchId;
    }

}
