package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

public class DisconnectPayload implements IChessMessage {

    private UUID userId;

    private long matchId;

    public DisconnectPayload(UUID userId, long matchId) {
        this.userId = userId;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

    @Override
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}
