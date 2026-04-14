package net.chess_platform.keycloak;

public record KeycloakUserVerifiedMessage(String id, String username, String displayName, String email, String avatar,
        String realm) {

}
