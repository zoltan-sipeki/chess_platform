package net.chess_platform.matchmaking_connection_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_connection_service.dto.request.PrivateMatchRequest;
import net.chess_platform.matchmaking_connection_service.dto.response.MMTokenResponse;
import net.chess_platform.matchmaking_connection_service.integration.MatchmakingServiceProxy;

@RestController
@RequestMapping("/api/queues")
public class QueueController {

    private MatchmakingServiceProxy matchmakingService;

    public QueueController(MatchmakingServiceProxy matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/{matchType}/members")
    public MMTokenResponse enqueue(@PathVariable @Pattern(regexp = "ranked|unranked") String matchType,
            CurrentUser currentUser) {
        var matchmakingToken = matchmakingService.enqueue(currentUser, matchType);
        return new MMTokenResponse(matchmakingToken);
    }

    @DeleteMapping("/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dequeue(CurrentUser currentUser) {
        matchmakingService.dequeue(currentUser);
    }

    @PostMapping("/private")
    @ResponseStatus(HttpStatus.CREATED)
    public MMTokenResponse startPrivateMatch(@RequestBody @Valid PrivateMatchRequest dto,
            CurrentUser currentUser) {
        var matchmakingToken = matchmakingService.startPrivateMatch(currentUser, dto.inviteeId());
        return new MMTokenResponse(matchmakingToken);
    }

}
