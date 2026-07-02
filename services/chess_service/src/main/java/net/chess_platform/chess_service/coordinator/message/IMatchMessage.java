package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

public interface IMatchMessage {

    public UUID getPlayerId();

    public long getMatchId();
}
