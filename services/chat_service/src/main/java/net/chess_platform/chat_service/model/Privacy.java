package net.chess_platform.chat_service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Privacy extends AuditedEntity {

    public static class Restriction {

        public enum Setting {
            PUBLIC,
            PRIVATE,
            FRIENDS
        }

        public enum Resource {
            FRIENDS
        }

        private Resource resource;

        private Setting restriction;

        public Restriction() {
        }

        public Restriction(Resource resource, Setting restriction) {
            this.resource = resource;
            this.restriction = restriction;
        }

        public Resource getResource() {
            return resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }

        public Setting getSetting() {
            return restriction;
        }

        public void setSetting(Setting restriction) {
            this.restriction = restriction;
        }
    }

    private UUID id = UUID.randomUUID();

    private UUID userId;

    private List<Restriction> restrictions;

    public Restriction findRestriction(Restriction.Resource resource) {
        for (var restriction : restrictions) {
            if (restriction.getResource() == resource) {
                return restriction;
            }
        }
        return null;
    }

    public void addRestriction(Restriction restriction) {
        if (restrictions == null) {
            restrictions = new ArrayList<>();
        }

        restrictions.add(restriction);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

}
