package net.chess_platform.matchmaking_service.job;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.queue.MatchFoundBroadcastEvent;
import net.chess_platform.matchmaking_service.integration.RelayServiceProxy;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.mmqueue.Player;
import net.chess_platform.matchmaking_service.service.MatchmakingService;

@Component
public class ExpandMmrRangesJob {

    private final MatchmakingService matchmakingService;

    private final RelayServiceProxy relayService;

    public ExpandMmrRangesJob(MatchmakingService matchmakingService,
            RelayServiceProxy relayService) {
        this.matchmakingService = matchmakingService;
        this.relayService = relayService;
    }

    @Scheduled(fixedRateString = "${matchmaking.expand-mmr-range-interval-ms}", initialDelayString = "${matchmaking.expand-mmr-range-interval-ms}")
    public void expandUnrankedMmrRanges() {
        var matches = matchmakingService.expandUnrankedMmrRanges();
        broadcastMatchFound(matches);

        matches = matchmakingService.expandRankedMmrRanges();
        broadcastMatchFound(matches);
    }

    private void broadcastMatchFound(List<Match> matches) {
        for (var match : matches) {
            var playerIds = match.getPlayers().stream().map(Player::getId).toList();
            var event = new MatchFoundBroadcastEvent(playerIds, match.getMatchmakingTokens());
            relayService.publish(event);
        }
    }
}
