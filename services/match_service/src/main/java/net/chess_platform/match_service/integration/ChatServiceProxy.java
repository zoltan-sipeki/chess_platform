package net.chess_platform.match_service.integration;

import java.util.List;
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

    private static record RelationShipSearchRequest(List<UUID> ids) {
    }

    private static record RelationShipSearchResponse(List<UUID> ids, String relationship) {
    }

    public boolean areFriends(UUID userId1, UUID userId2) {
        var response = restClient.post().uri(uri -> uri.path("/api/relationships/search").build())
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .body(new RelationShipSearchRequest(List.of(userId1, userId2))).retrieve()
                .toEntity(RelationShipSearchResponse.class);

        return response.getBody().relationship().equals("FRIENDS");
    }

}
