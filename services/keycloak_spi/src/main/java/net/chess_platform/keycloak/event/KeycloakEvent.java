package net.chess_platform.keycloak.event;

import java.util.UUID;

public class KeycloakEvent<T> {

    public enum Type {
        USER_VERIFIED,
        USER_UPDATED
    }

    private UUID id = UUID.randomUUID();

    private Type type;

    private T payload;

    protected KeycloakEvent(Type type, T payload) {
        this.type = type;
        this.payload = payload;
    }

    public UUID getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }

}
