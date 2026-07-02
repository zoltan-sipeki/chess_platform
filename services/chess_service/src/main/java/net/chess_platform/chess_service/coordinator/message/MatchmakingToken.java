package net.chess_platform.chess_service.coordinator.message;

import java.util.UUID;

import net.chess_platform.chess_service.coordinator.match.Match;

public class MatchmakingToken implements IRoutableMessage {

    private long matchId;

    private UUID playerId;

    private Integer mmr;

    private Match.Type matchType;

    private UUID target;

    public MatchmakingToken(long matchId, UUID userId, Integer mmr, Match.Type matchType, UUID target) {
        this.matchId = matchId;
        this.playerId = userId;
        this.mmr = mmr;
        this.matchType = matchType;
        this.target = target;
    }

    public Integer getMmr() {
        return mmr;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getMatchId() {
        return matchId;
    }

    public Match.Type getMatchType() {
        return matchType;
    }

    public UUID getTarget() {
        return target;
    }

    @Override
    public long getRoutingKey() {
        return matchId;
    }

}