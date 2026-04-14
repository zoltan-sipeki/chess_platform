package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

public class ReconnectPayload implements IChessMessage {

    private UUID userId;

    private Long matchId;

    public ReconnectPayload() {
    }

    public ReconnectPayload(UUID userId, Long matchId) {
        this.userId = userId;
        this.matchId = matchId;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public long getMatchId() {
        return matchId;
    }

}
