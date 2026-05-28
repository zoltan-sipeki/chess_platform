package net.chess_platform.keycloak.event;

public class KeycloakUserVerifiedEvent extends KeycloakEvent<KeycloakUser> {

    public KeycloakUserVerifiedEvent(KeycloakUser payload) {
        super(Type.USER_VERIFIED, payload);
    }
}
