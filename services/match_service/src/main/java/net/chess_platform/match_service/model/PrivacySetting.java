package net.chess_platform.match_service.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class PrivacySetting extends AuditedEntity {

    public enum Resource {
        MATCH_HISTORY,
        MATCH_STATS,
        MMR
    }

    public enum Restriction {
        PUBLIC,
        PRIVATE,
        FRIENDS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MatchUser user;

    @Enumerated(EnumType.STRING)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    private Restriction restriction = Restriction.PUBLIC;

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

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

}
