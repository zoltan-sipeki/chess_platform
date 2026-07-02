package net.chess_platform.matchmaking_service.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_service.dto.UpdateMatchRoutingDto;
import net.chess_platform.matchmaking_service.model.MatchRouting;
import net.chess_platform.matchmaking_service.service.MatchmakingService;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @PatchMapping("/matches/{matchId}")
    public void updateMatchStatus(@PathVariable long matchId, @RequestBody UpdateMatchRoutingDto dto,
            CurrentUser currentUser) {
        var update = new MatchRouting.Update();
        update.setMatchStatus(dto.matchStatus());

        matchmakingService.updateMatchRouting(matchId, update, currentUser);
    }
}
