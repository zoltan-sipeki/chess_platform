package net.chess_platform.chess_service.ws.message.client;

import java.util.UUID;

import net.chess_platform.chess_service.chess.move.Position;

public class MovePayload implements IChessMessage {

    private UUID userId;

    private Long matchId;

    private Position from;

    private Position to;

    public MovePayload() {
    }

    public MovePayload(long matchId, Position from, Position to) {
        this.matchId = matchId;
        this.from = from;
        this.to = to;
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

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }
}
