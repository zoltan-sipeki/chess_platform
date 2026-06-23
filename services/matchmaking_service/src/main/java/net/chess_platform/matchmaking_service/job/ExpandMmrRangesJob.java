package net.chess_platform.matchmaking_service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.matchmaking_service.service.MatchmakingService;

@Component
public class ExpandMmrRangesJob {

    private final MatchmakingService matchmakingService;

    public ExpandMmrRangesJob(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @Scheduled(fixedRateString = "${matchmaking.expand-mmr-range-interval-ms}", initialDelayString = "${matchmaking.expand-mmr-range-interval-ms}")
    public void expandMmrRanges() {
        matchmakingService.expandUnrankedMmrRanges();
        matchmakingService.expandRankedMmrRanges();
    }
}
