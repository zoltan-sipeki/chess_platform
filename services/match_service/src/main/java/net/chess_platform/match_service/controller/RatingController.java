package net.chess_platform.match_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PlayerMmrDto;
import net.chess_platform.match_service.service.PlayerMmrService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final PlayerMmrService service;

    public RatingController(PlayerMmrService service) {
        this.service = service;
    }

    @GetMapping(params = { "userId" })
    public PlayerMmrDto getUserById(@RequestParam UUID userId, CurrentUser currentUser) {
        return service.find(userId, currentUser);
    }

}
