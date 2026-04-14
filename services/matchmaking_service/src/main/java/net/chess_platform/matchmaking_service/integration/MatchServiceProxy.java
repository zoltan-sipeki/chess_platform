package net.chess_platform.matchmaking_service.integration;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MatchServiceProxy {

    private final RestClient restClient;

    public MatchServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://match-service").build();
    }

    public record MatchUserResponse(UUID id, int unrankedMmr, int rankedMmr, OffsetDateTime lastPlayed) {
    }

    public record OngoingMatchResponse(long matchId, UUID userId, String instance) {

    }

    public OngoingMatchResponse findOngoingMatchByUserId(UUID userId) {
        return restClient.get()
                .uri(uri -> uri.path("/api/matches/ongoing").queryParam("userId", userId).build())
                .retrieve()
                .body(OngoingMatchResponse.class);
    }
}
