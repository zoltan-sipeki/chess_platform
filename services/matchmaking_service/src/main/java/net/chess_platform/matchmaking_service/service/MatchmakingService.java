package net.chess_platform.matchmaking_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.chess_platform.common.domain_events.broker.queue.DequeueEvent;
import net.chess_platform.common.domain_events.broker.queue.EnqueueEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.common.domain_events.broker.queue.User;
import net.chess_platform.common.domain_events.broker.relay.RelayDisconnectEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_service.integration.MatchServiceProxy;
import net.chess_platform.matchmaking_service.mmqueue.MMQueue;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.mmqueue.MatchmakingToken;
import net.chess_platform.matchmaking_service.mmqueue.Player;
import net.chess_platform.matchmaking_service.repository.PlayerRepository;

@Service
public class MatchmakingService {

    private final MMQueue unrankedQueue;

    private final MMQueue rankedQueue;

    private final MMTokenParser matchmakingTokenService;

    private final EurekaClient discoveryClient;

    private final MatchServiceProxy matchService;

    private final DomainEventService eventService;

    private final PlayerRepository playerRepository;

    public MatchmakingService(@Qualifier("unrankedQueue") MMQueue unrankedQueue,
            @Qualifier("rankedQueue") MMQueue rankedQueue,
            MMTokenParser jwtService,
            EurekaClient discoveryClient, MatchServiceProxy matchService, DomainEventService eventService,
            PlayerRepository playerRepository) {
        this.unrankedQueue = unrankedQueue;
        this.rankedQueue = rankedQueue;
        this.matchmakingTokenService = jwtService;
        this.discoveryClient = discoveryClient;
        this.matchService = matchService;
        this.eventService = eventService;
        this.playerRepository = playerRepository;
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

        Match match = null;
        if (queueType == Match.Type.UNRANKED) {
            match = unrankedQueue.addPlayer(userId);
        } else {
            match = rankedQueue.addPlayer(userId);
        }

        if (match == null) {
            eventService.publish(
                    new EnqueueEvent(userId));
        } else {
            var tokens = createMMTokens(match);

            for (var token : tokens) {
                if (!userId.equals(token.playerId())) {
                    eventService.publish(new DequeueEvent(token.playerId()));
                }

                var payload = new MatchFoundEvent.Payload.Builder(token.jwt()).build();
                eventService.publish(
                        new MatchFoundEvent(List.of(token.playerId()), payload));
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

    public List<MatchmakingToken> expandRankedMmrRanges() {
        return expandMmrRanges(rankedQueue);
    }

    public List<MatchmakingToken> expandUnrankedMmrRanges() {
        return expandMmrRanges(unrankedQueue);
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

        var match = new Match(List.of(new Player(inviterId), new Player(inviteeId)), Match.Type.PRIVATE);
        var tokens = createMMTokens(match);

        var players = playerRepository.findAllById(List.of(inviterId, inviteeId));

        for (var token : tokens) {

            net.chess_platform.matchmaking_service.model.Player otherPlayer = null;
            for (var p : players) {
                if (!p.getId().equals(token.playerId())) {
                    otherPlayer = p;
                    break;
                }
            }

            var u = new User(otherPlayer.getId(), otherPlayer.getDisplayName(), otherPlayer.getAvatar());
            var payload = new MatchFoundEvent.Payload.Builder(token.jwt());

            if (token.playerId().equals(inviterId)) {
                payload.invitee(u);
            } else {
                payload.inviter(u);
            }

            eventService.publish(
                    new MatchFoundEvent(List.of(token.playerId()), payload.build()));
        }
    }

    public List<MatchmakingToken> createMMTokens(Match match) {
        InstanceInfo instanceInfo = null;
        try {
            instanceInfo = discoveryClient.getNextServerFromEureka("chess-service", false);
        } catch (RuntimeException e) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
        var target = instanceInfo.getMetadata().get("uuid");
        long matchId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        var matchType = match.getMatchType();
        var players = match.getPlayers();

        var tokens = new ArrayList<MatchmakingToken>();
        for (var player : players) {
            var token = matchmakingTokenService.createMatchmakingToken(player, matchType, matchId, target);
            tokens.add(new MatchmakingToken(player.getId(), token));
        }

        return tokens;
    }

    private List<MatchmakingToken> expandMmrRanges(MMQueue queue) {
        var matches = queue.expandSearchRanges();
        var result = new ArrayList<MatchmakingToken>();

        for (var match : matches) {
            var tokens = createMMTokens(match);
            result.addAll(tokens);
            for (var token : tokens) {
                eventService.publish(new DequeueEvent(token.playerId()));

                var payload = new MatchFoundEvent.Payload.Builder(token.jwt()).build();
                eventService.publish(
                        new MatchFoundEvent(List.of(token.playerId()), payload));
            }
        }

        return result;
    }

    private boolean isInMatch(UUID userId) {
        try {
            matchService.findOngoingMatchByUserId(userId);
            return true;
        } catch (ResourceAccessException | HttpServerErrorException e) {
            throw new ServiceUnavailableException("Matchmaking service is unavailable. Please try again later.");
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }

    }

    private boolean isInQueue(UUID userId) {
        return unrankedQueue.isInQueue(userId) || rankedQueue.isInQueue(userId);
    }

    public void process(RelayDisconnectEvent e) {
        dequeuePlayer(e.getData().userId());
    }
}
