package net.chess_platform.match_service.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.LeaderboardEntryDto;
import net.chess_platform.match_service.service.LeaderboardService;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public List<LeaderboardEntryDto> getLeaderBoard(
            @PageableDefault(size = 100) @SortDefaults({
                    @SortDefault(sort = "mmr", direction = Direction.DESC),
                    @SortDefault(sort = "player.displayName", direction = Direction.ASC) }) Pageable pageable,
            CurrentUser currentUser) {
        return leaderboardService.fetchLeaderboard(pageable, currentUser);
    }

}
