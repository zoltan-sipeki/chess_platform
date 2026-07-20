package net.chess_platform.matchmaking_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.queue.DequeueEvent;
import net.chess_platform.common.domain_events.broker.queue.EnqueueEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.common.domain_events.broker.queue.User;
import net.chess_platform.common.domain_events.broker.relay.RelayDisconnectEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_service.exception.EntityNotFoundException;
import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_service.integration.ChatServiceProxy;
import net.chess_platform.matchmaking_service.mmqueue.MMQueue;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.model.MatchRouting;
import net.chess_platform.matchmaking_service.model.MatchRoutingFactory;
import net.chess_platform.matchmaking_service.model.Player;
import net.chess_platform.matchmaking_service.repository.MatchRoutingRepository;
import net.chess_platform.matchmaking_service.repository.PlayerRepository;
import net.chess_platform.matchmaking_service.service.PermissionService.Action;

@Service
public class MatchmakingService {

    private final MMQueue unrankedQueue;

    private final MMQueue rankedQueue;

    private final MatchRoutingFactory matchRoutingFactory;

    private final MatchRoutingRepository matchRoutingRepository;

    private final EurekaClient discoveryClient;

    private final DomainEventService eventService;

    private final PlayerRepository playerRepository;

    private final PermissionService permissionService;

    public MatchmakingService(@Qualifier("unrankedQueue") MMQueue unrankedQueue,
            @Qualifier("rankedQueue") MMQueue rankedQueue,
            MatchRoutingFactory machRoutingFactory,
            EurekaClient discoveryClient, DomainEventService eventService,
            PlayerRepository playerRepository, MatchRoutingRepository matchRoutingRepository,
            PermissionService permissionService, ChatServiceProxy chatServiceProxy) {
        this.unrankedQueue = unrankedQueue;
        this.rankedQueue = rankedQueue;
        this.matchRoutingFactory = machRoutingFactory;
        this.discoveryClient = discoveryClient;
        this.eventService = eventService;
        this.playerRepository = playerRepository;
        this.matchRoutingRepository = matchRoutingRepository;
        this.permissionService = permissionService;
    }

    public void enqueuePlayer(UUID userId, Match.Type queueType) {
        if (queueType == Match.Type.PRIVATE) {
            throw new MatchmakingException(
                    "Only ranked or unranked matches are supported");
        }

        if (isInQueue(userId)) {
            throw new MatchmakingException(
                    "Already in queue");
        }

        if (isInMatch(userId)) {
            throw new MatchmakingException(
                    "Already in match");
        }

        var player = playerRepository.findById(userId).orElseThrow(() -> new MatchmakingException(
                "Player not found"));

        Match match = null;
        if (queueType == Match.Type.UNRANKED) {
            match = unrankedQueue.addPlayer(player.clone());
        } else {
            match = rankedQueue.addPlayer(player.clone());
        }

        if (match == null) {
            eventService.publish(
                    new EnqueueEvent(userId));
            return;
        }

        var routingData = createRoutingData(match);

        try {
            save(routingData);

            for (var data : routingData) {
                sendMatchFoundEvent(data);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            for (var data : routingData) {
                if (!userId.equals(data.getPlayerId())) {
                    eventService.publish(new DequeueEvent(data.getPlayerId()));
                }
            }
        }
    }

    public boolean dequeuePlayer(UUID userId) {
        if (userId == null) {
            return false;
        }

        boolean dequeued = unrankedQueue.removePlayer(userId);
        if (dequeued) {
            eventService.publish(new DequeueEvent(userId));
            return true;
        }

        dequeued = rankedQueue.removePlayer(userId);
        if (dequeued) {
            eventService.publish(new DequeueEvent(userId));
            return true;
        }

        return false;
    }

    public void expandRankedMmrRanges() {
        expandMmrRanges(rankedQueue);
    }

    public void expandUnrankedMmrRanges() {
        expandMmrRanges(unrankedQueue);
    }

    public void startPrivateMatch(UUID inviterId, UUID inviteeId) {
        if (inviterId.equals(inviteeId)) {
            throw new MatchmakingException(
                    "Cannot invite yourself");
        }

        if (isInQueue(inviterId)) {
            throw new MatchmakingException("Inviter is in queue");
        }

        if (isInQueue(inviteeId)) {
            throw new MatchmakingException(
                    "Invitee is in queue");
        }

        if (isInMatch(inviterId)) {
            throw new MatchmakingException(
                    "Inveter is in match");
        }

        if (isInMatch(inviteeId)) {
            throw new MatchmakingException(
                    "Invitee is in match");
        }

        var players = playerRepository.findAllById(List.of(inviterId, inviteeId));
        var match = new Match(players, Match.Type.PRIVATE);
        var routingData = createRoutingData(match);

        try {
            save(routingData);
            for (var d : routingData) {

                var payload = new MatchFoundEvent.Payload.Builder(d.getToken(), d.getTarget());

                for (var p : players) {
                    var u = new User(p.getId(), p.getDisplayName(), p.getAvatar());
                    if (p.getId().equals(inviterId)) {
                        payload.inviter(u);
                    } else {
                        payload.invitee(u);
                    }
                }

                eventService.publish(
                        new MatchFoundEvent(List.of(d.getPlayerId()), payload.build()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MatchRouting> createRoutingData(Match match) {
        InstanceInfo instanceInfo = null;
        try {
            instanceInfo = discoveryClient.getNextServerFromEureka("chess-service", false);
        } catch (RuntimeException e) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
        var target = UUID.fromString(instanceInfo.getMetadata().get("uuid"));
        long matchId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        var matchType = match.getMatchType();
        var players = match.getPlayers();

        var result = new ArrayList<MatchRouting>();
        for (var player : players) {
            MatchRouting data;
            if (match.getMatchType() == Match.Type.PRIVATE) {
                data = matchRoutingFactory.create(player, matchId, matchType, target, players.get(0).getId(),
                        players.get(1).getId());
            } else {
                data = matchRoutingFactory.create(player, matchId, matchType, target);
            }

            result.add(data);
        }

        return result;
    }

    public void process(RelayDisconnectEvent e) {
        dequeuePlayer(e.getData().userId());
    }

    @Transactional
    public void process(MatchEndedEvent e) {
        var d = e.getData();
        var matchId = d.matchId();

        for (var p : d.players()) {
            var update = new Player.Update();
            var type = Match.Type.valueOf(d.matchType());
            if (type == Match.Type.RANKED) {
                update.setRankedMmr(p.mmrAfter());
            } else if (type == Match.Type.UNRANKED) {
                update.setUnrankedMmr(p.mmrAfter());
            }
            playerRepository.update(p.id(), update);
        }

        matchRoutingRepository.delete(matchId);
    }

    @Transactional
    public boolean declineMatch(UUID playerId) {
        long deletedCount = matchRoutingRepository.deletePending(playerId);
        return deletedCount > 0;
    }

    @Transactional
    public void cleanUpStaleRoutingData() {
        matchRoutingRepository.cleanUpStaleData();
    }

    @Transactional
    public void updateMatchRouting(long matchId, MatchRouting.Update update, CurrentUser user) {
        var auth = permissionService.authorize(Action.MATCH_ROUTING_UPDATE, user, null);
        long modifiedCount = matchRoutingRepository.update(matchId, update, auth);
        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    private void expandMmrRanges(MMQueue queue) {
        var matches = queue.expandSearchRanges();

        for (var match : matches) {
            var routingData = createRoutingData(match);
            try {
                save(routingData);
                for (var data : routingData) {
                    sendMatchFoundEvent(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                for (var data : routingData) {
                    eventService.publish(new DequeueEvent(data.getPlayerId()));
                }
            }
        }
    }

    private void sendMatchFoundEvent(MatchRouting routing) {
        var payload = new MatchFoundEvent.Payload.Builder(routing.getToken(), routing.getTarget()).build();
        eventService.publish(
                new MatchFoundEvent(List.of(routing.getPlayerId()), payload));
    }

    private boolean isInMatch(UUID userId) {
        return matchRoutingRepository.hasActiveMatch(userId);
    }

    private boolean isInQueue(UUID userId) {
        return unrankedQueue.isInQueue(userId) || rankedQueue.isInQueue(userId);
    }

    @Transactional
    private void save(List<MatchRouting> routingData) {
        for (var data : routingData) {
            matchRoutingRepository.save(data);
        }
    }
}
