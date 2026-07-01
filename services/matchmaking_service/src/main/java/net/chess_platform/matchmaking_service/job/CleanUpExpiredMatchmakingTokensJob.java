package net.chess_platform.matchmaking_service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.matchmaking_service.service.MatchmakingService;

@Component
public class CleanUpExpiredMatchmakingTokensJob {

    private final MatchmakingService matchmakingService;

    public CleanUpExpiredMatchmakingTokensJob(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @Scheduled(fixedRateString = "${matchmaking.routing-clean-up-interval-ms}", initialDelayString = "${matchmaking.routing-clean-up-interval-ms}")
    public void run() {
        matchmakingService.cleanUpStaleRoutingData();
    }
}
