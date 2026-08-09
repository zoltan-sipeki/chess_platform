package net.chess_platform.match_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.service.MatchService;
import net.chess_platform.match_service.service.PlayerService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final MatchService matchService;
    
    private final PlayerService playerService;

    public StatsController(MatchService matchService, PlayerService playerService) {
        this.matchService = matchService;
        this.playerService = playerService;
    }

    @GetMapping(value = "/match", params = { "userId" })
    public List<MatchStatsDto> getStatsByUserId(@RequestParam UUID userId, CurrentUser currentUser) {
        return matchService.findMatchStats(userId, currentUser);
    }

    @GetMapping(value ="/player", params = { "userId" })
    public PlayerStatsDto getPlayerStats(@RequestParam UUID userId, CurrentUser user) {
        return playerService.findPlayerStats(userId, user);
    }
}
