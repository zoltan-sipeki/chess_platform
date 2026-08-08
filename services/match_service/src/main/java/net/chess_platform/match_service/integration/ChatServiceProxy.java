package net.chess_platform.match_service.integration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatServiceProxy {

    private RestClient restClient;

    public ChatServiceProxy(@Qualifier("oauth2RestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://chat-service").build();
    }

    private static record RelationshipDto(String relationship) {
    }

    public boolean areFriends(UUID userId1, UUID userId2) {
        var response = restClient.get()
                .uri(uri -> uri.path("/api/relationships").queryParam("userId1", userId1).queryParam("userId2", userId2)
                        .build())
                .retrieve()
                .toEntity(RelationshipDto.class);

        return response.getBody().relationship().equals("FRIENDS");
    }

}
