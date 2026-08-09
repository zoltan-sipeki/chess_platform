package net.chess_platform.matchmaking_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_service.dto.CreatePrivateMatchDto;
import net.chess_platform.matchmaking_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_service.dto.UpdateMatchRoutingDto;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.model.MatchRouting;
import net.chess_platform.matchmaking_service.service.MatchmakingService;

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
        matchmakingService.enqueuePlayer(currentUser.id(), Match.Type.valueOf(matchType.toUpperCase()));
    }

    @DeleteMapping("/queues/members/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dequeue(CurrentUser currentUser) {
        matchmakingService.dequeuePlayer(currentUser.id());
    }

    @PostMapping("/private-match")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void startPrivateMatch(@RequestBody @Valid CreatePrivateMatchDto dto,
            CurrentUser currentUser) {
        matchmakingService.startPrivateMatch(currentUser.id(), dto.inviteeId());
    }

    @GetMapping("/current-match")
    public ResponseEntity<CurrentMatchDto> getActiveMatch(CurrentUser currentUser) {
        var currentMatch = matchmakingService.fetchCurrentMatch(currentUser);
        if (currentMatch == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(currentMatch);
    }

    @DeleteMapping("/current-match")
    public void deletePendingMatch(CurrentUser currentUser) {
        matchmakingService.declineMatch(currentUser.id());
    }

    @PatchMapping("/matches/{matchId}")
    public void updateMatchStatus(@PathVariable long matchId, @RequestBody UpdateMatchRoutingDto dto,
            CurrentUser currentUser) {
        var update = new MatchRouting.Update();
        update.setMatchStatus(dto.matchStatus());

        matchmakingService.updateMatchRouting(matchId, update, currentUser);
    }
}
