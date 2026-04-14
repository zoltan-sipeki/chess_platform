package net.chess_platform.gateway.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserServiceProxy {

    private final RestClient restClient;

    public UserServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://user-service").build();
    }

    public void verifyUser() {
        restClient.post().uri("/api/users/verify").retrieve().toBodilessEntity();
    }
}
