package net.chess_platform.keycloak;

import org.keycloak.models.UserModel;

public record KeycloakUserVerifiedMessage(String id, String username, String displayName, String email, String avatar, String realm) {

    public static KeycloakUserVerifiedMessage fromUserModel(String realmName, UserModel user) {
        var attributes = user.getAttributes();
        var displayName = attributes.get("displayName");
        var avatar = attributes.get("avatar");

        return new KeycloakUserVerifiedMessage(user.getId(), user.getUsername(),
                displayName == null || displayName.isEmpty() ? "" : displayName.getFirst(),
                user.getEmail(), avatar == null || avatar.isEmpty() ? "" : avatar.getFirst(), realmName);
    }
}
