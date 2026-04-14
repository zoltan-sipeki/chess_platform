package net.chess_platform.match_service.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class OngoingMatch extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private long matchId;

    @OneToOne(fetch = FetchType.LAZY)
    private MatchUser user;

    private String target;

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String nodeName) {
        this.target = nodeName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MatchUser getUser() {
        return user;
    }

    public void setUser(MatchUser user) {
        this.user = user;
    }
}
