package net.chess_platform.chess_service.coordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.client.RestClientException;

import net.chess_platform.chess_service.coordinator.match.Match;
import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;
import net.chess_platform.chess_service.coordinator.match.Player;
import net.chess_platform.chess_service.coordinator.match.event.FlagFallEvent;
import net.chess_platform.chess_service.coordinator.match.event.MatchTimeoutEvent;
import net.chess_platform.chess_service.coordinator.match.event.PromotionTimeoutEvent;
import net.chess_platform.chess_service.coordinator.message.DisconnectMessage;
import net.chess_platform.chess_service.coordinator.message.IMatchMessage;
import net.chess_platform.chess_service.coordinator.message.MatchmakingToken;
import net.chess_platform.chess_service.coordinator.message.MoveMessage;
import net.chess_platform.chess_service.coordinator.message.PromotionMessage;
import net.chess_platform.chess_service.coordinator.message.ReconnectMessage;
import net.chess_platform.chess_service.coordinator.message.ResignMessage;
import net.chess_platform.chess_service.integration.MatchmakingServiceProxy;
import net.chess_platform.chess_service.ws.PlayerConnections;
import net.chess_platform.chess_service.ws.message.server.ServerMessage;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;

public class MatchCoordinatorThread extends Thread {

    private final Map<Long, ScheduledFuture<?>> connectTimeouts = new HashMap<>();

    private final Map<Long, Match> matches = new HashMap<>();

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    private final EventQueue eventQueue;

    private final MatchmakingServiceProxy matchmakingService;

    private final DomainEventService eventService;

    private final PlayerConnections connections;

    private final Mapper mapper;

    public MatchCoordinatorThread(EventQueue eventQueue, PlayerConnections playerConnections,
            MatchmakingServiceProxy matchmakingService, Mapper mapper,
            DomainEventService eventService) {
        this.eventQueue = eventQueue;
        this.connections = playerConnections;
        this.matchmakingService = matchmakingService;
        this.mapper = mapper;
        this.eventService = eventService;
    }

    public long getMatchIdByUserId(UUID userId) {
        var player = players.get(userId);
        return player == null ? 0 : player.getMatchId();
    }

    @Override
    public void run() {
        while (true) {
            try {
                var event = eventQueue.take();
                switch (event) {
                    case DisconnectMessage m -> process(m);
                    case MatchmakingToken m -> process(m);
                    case IMatchMessage m -> process(m);
                    case FlagFallEvent m -> process(m);
                    case PromotionTimeoutEvent m -> process(m);
                    case MatchTimeoutEvent m -> process(m);
                    default -> throw new IllegalArgumentException("Invalid message type.");
                }

            } catch (InterruptedException e) {
            }
        }
    }

    public void enqueueMessage(Object message) {
        eventQueue.offer(message);
    }

    public void process(DisconnectMessage message) {
        var userId = message.getPlayerId();
        var player = players.get(userId);
        if (player == null) {
            return;
        }

        var match = matches.get(player.getMatchId());
        if (match == null) {
            return;
        }

        var otherPlayer = match.getPlayerOtherThan(userId);
        if (otherPlayer == null) {
            return;
        }

        connections.sendMessage(otherPlayer.getId(),
                new ServerMessage(ServerMessage.Type.PLAYER_DISCONNECTED, userId));
    }

    private void process(IMatchMessage message) {
        var matchId = message.getMatchId();
        var userId = message.getPlayerId();
        var match = matches.get(matchId);
        if (match == null) {
            connections.disconnect(userId);
            return;
        }

        if (match.findPlayer(userId) == null) {
            connections.disconnect(userId);
            return;
        }

        switch (message) {
            case ReconnectMessage m -> process(m, match);
            case MoveMessage m -> process(m, match);
            case PromotionMessage m -> process(m, match);
            case ResignMessage m -> process(m, match);
            default -> throw new IllegalArgumentException("Invalid message type.");
        }

    }

    private void process(MatchmakingToken token) {
        var userId = token.getPlayerId();
        var matchId = token.getMatchId();
        var match = matches.get(matchId);

        if (match == null) {
            match = new Match(matchId, token.getMatchType(), eventQueue);
            matches.put(matchId, match);
            setTimeout(matchId);
        }

        var player = new Player(userId, token.getMmr(), matchId);
        var prev = players.put(userId, player);
        if (prev != null) {
            return;
        }

        match.addPlayer(player);

        if (!match.isEveryBodyConnected()) {
            return;
        }

        clearTimeout(matchId);
        var players = match.getPlayers();
        try {
            matchmakingService.updateMatchRouting(matchId);
            match.start();
            sendToAllPlayers(match, new ServerMessage(ServerMessage.Type.GAME_STATE, mapper.toGameStateDto(match)));
        } catch (RestClientException e) {
            for (var p : players) {
                var id = p.getId();
                connections.sendMessage(id,
                        new ServerMessage(ServerMessage.Type.ERROR, "Failed to create match. Closing game."));
                connections.disconnect(id);
                e.printStackTrace();
            }

            cleanUp(match);
        }
    }

    private void process(ReconnectMessage message, Match match) {
        var userId = message.getPlayerId();

        var otherPlayer = match.getPlayerOtherThan(userId);
        if (otherPlayer != null) {
            connections.sendMessage(otherPlayer.getId(),
                    new ServerMessage(ServerMessage.Type.PLAYER_RECONNECTED, userId));
        }

        var gameState = mapper.toGameStateDto(match);

        connections.sendMessage(userId,
                new ServerMessage(ServerMessage.Type.GAME_STATE, gameState));
    }

    private void process(MoveMessage message, Match match) {
        var moveResult = match.process(message);

        sendMoveResult(match, moveResult);

        if (moveResult.isGameOver()) {
            handleGameOver(match);
        }
    }

    private void process(PromotionMessage message, Match match) {
        var moveResult = match.process(message);

        sendMoveResult(match, moveResult);

        if (moveResult.isGameOver()) {
            handleGameOver(match);
        }
    }

    private void process(ResignMessage message, Match match) {
        var moveResult = match.process(message);

        sendMoveResult(match, moveResult);
        handleGameOver(match);
    }

    private void process(FlagFallEvent e) {
        var match = matches.get(e.matchId());
        sendMoveResult(match, e.moveResult());
        handleGameOver(match);
    }

    private void process(MatchTimeoutEvent e) {
        var match = matches.get(e.matchId());
        var players = match.getPlayers();

        for (var player : players) {
            var userId = player.getId();
            connections.sendMessage(userId, new ServerMessage(ServerMessage.Type.MATCH_TIMEOUT,
                    "The opponent took too long to connect. Closing match."));
            connections.disconnect(userId);
        }

        cleanUp(match);
    }

    private void process(PromotionTimeoutEvent e) {
        var match = matches.get(e.matchId());
        sendMoveResult(match, e.moveResult());
        handleGameOver(match);
    }

    private void setTimeout(long matchId) {
        var timeout = eventQueue.schedule(() -> new MatchTimeoutEvent(matchId), 60000, TimeUnit.MILLISECONDS);
        connectTimeouts.put(matchId, timeout);
    }

    private void clearTimeout(long matchId) {
        var timeout = connectTimeouts.remove(matchId);
        timeout.cancel(true);
    }

    private <T> void sendToAllPlayers(Match match, ServerMessage message) {
        var players = match.getPlayers();
        for (var p : players) {
            connections.sendMessage(p.getId(), message);
        }
    }

    private void handleGameOver(Match match) {
        var matchPlayers = match.getPlayers();
        for (var p : matchPlayers) {
            connections.disconnect(p.getId());
        }

        var matchResult = mapper.toMatchResult(match);
        eventService.publish(new MatchEndedEvent(matchResult));

        cleanUp(match);
    }

    private void cleanUp(Match match) {
        var matchPlayers = match.getPlayers();
        for (var p : matchPlayers) {
            players.remove(p.getId());
        }
        matches.remove(match.getId());
    }

    private void sendMoveResult(Match match, MoveProcessingResult moveResult) {
        sendToAllPlayers(match, new ServerMessage(ServerMessage.Type.MOVE_RESULT, mapper.toDto(moveResult)));
    }

}
