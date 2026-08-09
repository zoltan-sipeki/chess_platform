package net.chess_platform.matchmaking_service.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import net.chess_platform.matchmaking_service.mmqueue.Match;

@Entity
public class MatchRouting extends AuditedEntity {

    public static class Update {

        private Status matchStatus;

        public Status getMatchStatus() {
            return matchStatus;
        }

        public void setMatchStatus(Status matchStatus) {
            this.matchStatus = matchStatus;
        }
    }

    public enum Status {
        PENDING, ACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID playerId;

    private long matchId;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    private Player inviter;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    private Player invitee;
    
    @Enumerated(EnumType.STRING)
    private Match.Type matchType;

    private UUID target;

    private Integer mmr;

    private Instant expiresAt;

    private String token;

    @Enumerated(EnumType.STRING)
    private Status matchStatus = Status.PENDING;

    protected MatchRouting() {
    }

    protected MatchRouting(Player player, long matchId,
            Match.Type matchType, UUID target,
            Instant expiresAt) {
        Integer mmr = null;
        if (matchType == Match.Type.RANKED) {
            mmr = player.getRankedMmr();
        } else if (matchType == Match.Type.UNRANKED) {
            mmr = player.getUnrankedMmr();
        }

        this.playerId = player.getId();
        this.matchId = matchId;
        this.matchType = matchType;
        this.target = target;
        this.mmr = mmr;
        this.expiresAt = expiresAt;
    }

    public Player getInviter() {
        return inviter;
    }

    public Player getInvitee() {
        return invitee;
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

    public Integer getMmr() {
        return mmr;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Status getMatchStatus() {
        return matchStatus;
    }

    protected void setToken(String jwt) {
        this.token = jwt;
    }

    protected void setInviter(Player inviter) {
        this.inviter = inviter;
    }

    protected void setInvitee(Player invitee) {
        this.invitee = invitee;
    }
}