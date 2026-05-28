package net.chess_platform.keycloak.event;

public class KeycloakUserUpdatedEvent extends KeycloakEvent<KeycloakUser> {

    public KeycloakUserUpdatedEvent(KeycloakUser payload) {
        super(Type.USER_UPDATED, payload);
    }
}
