package net.chess_platform.chess_service.coordinator;

import java.util.UUID;

import net.chess_platform.chess_service.coordinator.match.Match;
import net.chess_platform.chess_service.ws.message.client.IChessMessage;

public class MatchmakingToken implements IChessMessage {

    private long matchId;

    private UUID userId;

    private Integer mmr;

    private Match.Type matchType;

    private String target;

    public MatchmakingToken(long matchId, UUID userId, Integer mmr, Match.Type matchType, String target) {
        this.matchId = matchId;
        this.userId = userId;
        this.mmr = mmr;
        this.matchType = matchType;
        this.target = target;
    }

    public Integer getMmr() {
        return mmr;
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    public long getMatchId() {
        return matchId;
    }

    public Match.Type getMatchType() {
        return matchType;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

}