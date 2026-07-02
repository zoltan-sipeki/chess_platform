package net.chess_platform.chess_service.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MatchmakingServiceProxy {

    private final RestClient restClient;

    private enum MatchStatus {
        ACTIVE
    }

    private static record UpdateMatchRoutingDto(MatchStatus matchStatus) {
    }

    public MatchmakingServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://matchmaking-service").build();
    }

    public void updateMatchRouting(long matchId) {
        restClient.put().uri("/api/matchmaking/matches/{matchId}", matchId)
                .body(new UpdateMatchRoutingDto(MatchStatus.ACTIVE)).retrieve();
    }
}
