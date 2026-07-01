package net.chess_platform.matchmaking_api_service.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Player extends AuditedEntity {

    @Id
    private UUID id;

    private String displayName;

    private String avatar;

    private int rankedMmr = 1500;

    private int unrankedMmr = 1500;

    public UUID getId() {
        return id;
    }

    public int getRankedMmr() {
        return rankedMmr;
    }

    public int getUnrankedMmr() {
        return unrankedMmr;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatar() {
        return avatar;
    }
}
