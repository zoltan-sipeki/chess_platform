package net.chess_platform.matchmaking_service.mmqueue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.repository.MatchmakingUserRepository;

public class MMQueue {

    @Value("${matchmaking.max-time-in-queue-ms}")
    private int MAX_TIME_IN_QUEUE_MS;

    private final Map<UUID, Player> unorderedQueue = new HashMap<>();

    private final TreeSet<Player> orderedQueue = new TreeSet<>((a, b) -> a.compareTo(b));

    private final Match.Type matchType;

    private final MatchmakingUserRepository matchmakingUserRepository;

    public MMQueue(Match.Type queueType, MatchmakingUserRepository matchmakingUserRepository) {
        this.matchType = queueType;
        this.matchmakingUserRepository = matchmakingUserRepository;
    }

    public synchronized Match addPlayer(UUID userId) {
        if (unorderedQueue.get(userId) != null) {
            return null;
        }

        var matchUser = matchmakingUserRepository.findById(userId)
                .orElseThrow(() -> new MatchmakingException("Player with id " + userId + " not found"));

        var player = new Player(userId, matchUser.getRankedMmr(), matchUser.getUnrankedMmr());

        return reAddPlayer(player);
    }

    private Match reAddPlayer(Player player) {
        if (player.getLastExpanded() == null) {
            var mmr = matchType == Match.Type.RANKED ? player.getRankedMmr() : player.getUnrankedMmr();
            player.setSearchRange(new SearchRange(mmr));
            player.setLastExpanded(LocalDateTime.now());
        }

        if (orderedQueue.contains(player)) {
            var otherPlayer = orderedQueue.ceiling(player);
            unorderedQueue.remove(player.getId());
            unorderedQueue.remove(otherPlayer.getId());
            orderedQueue.remove(otherPlayer);

            onPlayerRemove(player);
            onPlayerRemove(otherPlayer);

            return new Match(List.of(player, otherPlayer), matchType);
        }

        orderedQueue.add(player);
        unorderedQueue.put(player.getId(), player);

        return null;
    }

    public synchronized boolean removePlayer(UUID userId) {
        var queuedPlayer = unorderedQueue.get(userId);
        if (queuedPlayer == null) {
            return false;
        }

        unorderedQueue.remove(queuedPlayer.getId());
        orderedQueue.remove(queuedPlayer);
        onPlayerRemove(queuedPlayer);

        return true;
    }

    public synchronized List<Match> expandSearchRanges() {
        var matches = new ArrayList<Match>();

        for (var entries : unorderedQueue.entrySet()) {
            var player = entries.getValue();
            if (shouldExpandSearchRange(player)) {
                orderedQueue.remove(player);
                player.expandSearchRange();

                var match = reAddPlayer(player);
                matches.add(match);
            }
        }

        return matches;
    }

    public synchronized boolean isInQueue(UUID userId) {
        return unorderedQueue.containsKey(userId);
    }

    private void onPlayerRemove(Player player) {
        player.setLastExpanded(null);
        player.setSearchRange(null);
    }

    private boolean shouldExpandSearchRange(Player player) {
        return player.getLastExpanded().plus(MAX_TIME_IN_QUEUE_MS, ChronoUnit.MILLIS)
                .isBefore(LocalDateTime.now());
    }
}
