package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

public interface IChessMessage {

    public UUID getUserId();

    public void setUserId(UUID userId);

    public long getMatchId();
}
