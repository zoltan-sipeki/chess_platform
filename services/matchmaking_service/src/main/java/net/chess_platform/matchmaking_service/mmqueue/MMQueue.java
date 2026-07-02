package net.chess_platform.matchmaking_service.mmqueue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import net.chess_platform.matchmaking_service.model.Player;

public class MMQueue {

    @Value("${matchmaking.max-time-in-queue-ms}")
    private int MAX_TIME_IN_QUEUE_MS;

    private final Map<UUID, QueuePlayer> unorderedQueue = new HashMap<>();

    private final TreeSet<QueuePlayer> orderedQueue = new TreeSet<>((a, b) -> a.compareTo(b));

    private final Match.Type matchType;

    public MMQueue(Match.Type queueType) {
        this.matchType = queueType;
    }

    public synchronized Match addPlayer(Player player) {
        if (unorderedQueue.get(player.getId()) != null) {
            return null;
        }

        return reAddPlayer(new QueuePlayer(player));
    }

    public synchronized boolean removePlayer(UUID userId) {
        var queuedPlayer = unorderedQueue.get(userId);
        if (queuedPlayer == null) {
            return false;
        }

        unorderedQueue.remove(queuedPlayer.getId());
        orderedQueue.remove(queuedPlayer);
        
        return true;
    }

    public synchronized List<Match> expandSearchRanges() {
        var matches = new ArrayList<Match>();

        for (var entries : unorderedQueue.entrySet()) {
            var player = entries.getValue();
            if (!shouldExpandSearchRange(player)) {
                continue;
            }

            orderedQueue.remove(player);
            player.expandSearchRange();

            var match = reAddPlayer(player);
            if (match != null) {
                matches.add(match);
            }
        }

        return matches;
    }

    public synchronized boolean isInQueue(UUID userId) {
        return unorderedQueue.containsKey(userId);
    }

    private Match reAddPlayer(QueuePlayer player) {
        if (player.getLastExpandedAt() == null) {
            var mmr = matchType == Match.Type.RANKED ? player.getRankedMmr() : player.getUnrankedMmr();
            player.setSearchRange(new SearchRange(mmr));
            player.setLastExpandedAt(Instant.now());
        }

        if (orderedQueue.contains(player)) {
            var otherPlayer = orderedQueue.ceiling(player);
            unorderedQueue.remove(player.getId());
            unorderedQueue.remove(otherPlayer.getId());
            orderedQueue.remove(otherPlayer);

            return new Match(List.of(player.getPlayer(), otherPlayer.getPlayer()), matchType);
        }

        orderedQueue.add(player);
        unorderedQueue.put(player.getId(), player);

        return null;
    }

    private boolean shouldExpandSearchRange(QueuePlayer player) {
        return player.getLastExpandedAt().plus(MAX_TIME_IN_QUEUE_MS, ChronoUnit.MILLIS)
                .isBefore(Instant.now());
    }
}
