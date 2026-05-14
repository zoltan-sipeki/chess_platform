package net.chess_platform.gateway.integration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserServiceProxy {

    private final RestClient restClient;

    public static record ProfileUserDto(UUID id, String displayName, String avatar) {}

    public UserServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://user-service").build();
    }

    public ProfileUserDto getUserById(String id) {
        return restClient.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .body(ProfileUserDto.class);
    }
}
