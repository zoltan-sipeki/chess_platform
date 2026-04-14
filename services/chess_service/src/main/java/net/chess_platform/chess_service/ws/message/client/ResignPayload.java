package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

public class ResignPayload implements IChessMessage {

    private UUID userId;

    private Long matchId;

    public ResignPayload() {
    }

    public ResignPayload(Long matchId) {
        this.matchId = matchId;
    }

    @Override
    public long getMatchId() {
        return matchId;
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
