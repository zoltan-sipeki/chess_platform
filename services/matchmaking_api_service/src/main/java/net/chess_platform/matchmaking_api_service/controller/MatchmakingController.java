package net.chess_platform.matchmaking_api_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_api_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_api_service.dto.request.PrivateMatchRequest;
import net.chess_platform.matchmaking_api_service.service.MatchmakingService;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PostMapping("/queues/{matchType}/members")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void enqueue(@PathVariable @Pattern(regexp = "ranked|unranked") String matchType,
            CurrentUser currentUser) {
        matchmakingService.enqueue(currentUser, matchType);
    }

    @DeleteMapping("/queues/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dequeue(CurrentUser currentUser) {
        matchmakingService.dequeue(currentUser);
    }

    @PostMapping("/private-match")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void startPrivateMatch(@RequestBody @Valid PrivateMatchRequest dto,
            CurrentUser currentUser) {
        matchmakingService.createPrivateMatch(currentUser, dto.inviteeId());
    }

    @GetMapping("/current-match")
    public CurrentMatchDto getActiveMatch(CurrentUser currentUser) {
        return matchmakingService.findCurrentMatch(currentUser);
    }  

    @DeleteMapping("/current-match")
    public void deletePendingMatch(CurrentUser currentUser) {
        matchmakingService.deletePendingMatch(currentUser);
    }
}
