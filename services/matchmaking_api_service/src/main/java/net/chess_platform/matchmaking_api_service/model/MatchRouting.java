package net.chess_platform.matchmaking_api_service.model;

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

@Entity
public class MatchRouting extends AuditedEntity {

    public enum Status {
        PENDING, ACTIVE
    }

    public enum MatchType {
        RANKED, UNRANKED, PRIVATE
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
    private MatchType matchType;

    private UUID target;

    private Integer mmr;

    private Instant expiresAt;

    private String token;

    @Enumerated(EnumType.STRING)
    private Status matchStatus = Status.PENDING;

    public UUID getPlayerId() {
        return playerId;
    }

    public long getMatchId() {
        return matchId;
    }

    public MatchType getMatchType() {
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

    public Player getInviter() {
        return inviter;
    }

    public Player getInvitee() {
        return invitee;
    }
}