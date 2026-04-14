package net.chess_platform.user_service.integration;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import net.chess_platform.user_service.dto.KeycloakUserRepresentation;

@Service
public class KeycloakProxy {

    private RestClient restClient;

    public KeycloakProxy(@Qualifier("keyCloakRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public UUID createUser(KeycloakUserRepresentation user) {
        var response = restClient.post().uri("/admin/realms/chess/users")
                .contentType(MediaType.APPLICATION_JSON).body(user).retrieve()
                .toBodilessEntity();
        var pathSegments = response.getHeaders().getLocation().getPath().split("/");
        var userId = pathSegments[pathSegments.length - 1];
        sendVerifyEmail(userId);
        return UUID.fromString(userId);
    }

    public List<KeycloakUserRepresentation> getUnsyncedUsers() {
        return restClient.get().uri("/admin/realms/chess/users?q=synced:false").retrieve()
                .body(new ParameterizedTypeReference<List<KeycloakUserRepresentation>>() {
                });
    }

    public KeycloakUserRepresentation getUser(String userId) {
        return restClient.get().uri("/admin/realms/chess/users/{id}", userId).retrieve()
                .body(KeycloakUserRepresentation.class);
    }

    public void updateUser(KeycloakUserRepresentation user) {
        restClient.put().uri("/admin/realms/chess/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON).body(user).retrieve().toBodilessEntity();
    }

    public void deleteUser(String id) {
        restClient.delete().uri("/admin/realms/chess/users/{id}", id).retrieve().toBodilessEntity();
    }

    private void sendVerifyEmail(String userId) {
        restClient.put().uri("/admin/realms/chess/users/{userId}/send-verify-email", userId).retrieve()
                .toBodilessEntity();
    }

}
