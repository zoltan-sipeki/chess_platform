package net.chess_platform.match_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchHistoryListDto;
import net.chess_platform.match_service.dto.MatchHistorySearchParams;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.service.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping(params = { "userId" })
    public MatchHistoryListDto getMatchHistory(
            @RequestParam UUID userId,
            MatchHistorySearchParams searchParams,
            @SortDefault(sort = "match.startedAt", direction = Direction.DESC) Pageable pageable,
            CurrentUser currentUser) {
        return matchService.findMatchHistory(userId, searchParams, pageable, currentUser);
    }

    @GetMapping("/{matchId}/replay")
    public String getReplay(@PathVariable UUID matchId) {
        return matchService.findReplay(matchId);
    }

    @GetMapping(value = "/stats", params = { "userId" })
    public List<MatchStatsDto> getStatsByUserId(@RequestParam UUID userId, CurrentUser currentUser) {
        return matchService.findMatchStats(userId, currentUser);
    }

}
