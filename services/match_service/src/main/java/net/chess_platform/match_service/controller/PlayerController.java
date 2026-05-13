package net.chess_platform.match_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.service.PlayerService;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{playerId}/stats")
    public PlayerStatsDto getPlayerStats(@PathVariable UUID playerId, CurrentUser user) {
        return playerService.findPlayerStats(playerId, user);
    }
}
