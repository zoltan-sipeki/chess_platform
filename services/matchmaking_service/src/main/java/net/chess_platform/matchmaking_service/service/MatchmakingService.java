package net.chess_platform.matchmaking_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_service.integration.MatchServiceProxy;
import net.chess_platform.matchmaking_service.mmqueue.MMQueue;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.mmqueue.Player;

@Service
public class MatchmakingService {

    private final MMQueue unrankedQueue;

    private final MMQueue rankedQueue;

    private final MMTokenParser matchmakingTokenService;

    private final EurekaClient discoveryClient;

    private final MatchServiceProxy matchService;

    public MatchmakingService(@Qualifier("unrankedQueue") MMQueue unrankedQueue,
            @Qualifier("rankedQueue") MMQueue rankedQueue,
            MMTokenParser jwtService,
            EurekaClient discoveryClient, MatchServiceProxy matchService) {
        this.unrankedQueue = unrankedQueue;
        this.rankedQueue = rankedQueue;
        this.matchmakingTokenService = jwtService;
        this.discoveryClient = discoveryClient;
        this.matchService = matchService;
    }

    public Match enqueuePlayer(UUID userId, Match.Type queueType) {
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

        return match;
    }

    public boolean dequeuePlayer(UUID userId) {
        if (userId == null) {
            return false;
        }

        if (unrankedQueue.removePlayer(userId)) {
            return true;
        }

        return rankedQueue.removePlayer(userId);
    }

    public List<Match> expandRankedMmrRanges() {
        return expandMmrRanges(rankedQueue);
    }

    public List<Match> expandUnrankedMmrRanges() {
        return expandMmrRanges(unrankedQueue);
    }

    public Map<UUID, String> createMMTokensForPrivateMatch(UUID inviterId, UUID inviteeId) {
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
        return createMMTokens(match);
    }

    public Map<UUID, String> createMMTokens(Match match) {
        InstanceInfo instanceInfo = null;
        try {
            instanceInfo = discoveryClient.getNextServerFromEureka("chess-service", false);
        } catch (RuntimeException e) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
        var target = instanceInfo.getMetadata().get("uuid");
        long matchId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        var players = match.getPlayers();
        var matchType = match.getMatchType();

        var token1 = matchmakingTokenService.createMatchmakingToken(players.get(0), matchType, matchId,
                target);
        var token2 = matchmakingTokenService.createMatchmakingToken(players.get(1), matchType, matchId,
                target);

        return Map.of(players.get(0).getId(), token1, players.get(1).getId(), token2);
    }

    private List<Match> expandMmrRanges(MMQueue queue) {
        var matches = queue.expandSearchRanges();
        for (var match : matches) {
            var tokens = createMMTokens(match);
            match.setMatchmakingTokens(tokens);
        }

        return matches;
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
}
