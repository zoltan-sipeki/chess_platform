package net.chess_platform.match_service.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchHistoryListDto;
import net.chess_platform.match_service.dto.MatchHistorySearchParams;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.mapper.MatchMapper;
import net.chess_platform.match_service.service.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private MatchMapper mapper;

    private MatchService matchService;

    public MatchController(MatchMapper mapper, MatchService matchService) {
        this.mapper = mapper;
        this.matchService = matchService;
    }

    @GetMapping(params = { "userId" })
    public MatchHistoryListDto getMatchHistory(
            @RequestParam UUID userId,
            MatchHistorySearchParams searchParams,
            Pageable pageable,
            CurrentUser currentUser) {
        return matchService.findMatchHistory(userId, searchParams, pageable, currentUser);
    }

    @GetMapping("/{matchId}/replay")
    public String getReplay(@PathVariable UUID matchId) {
        return matchService.findReplay(matchId);
    }

    @GetMapping("/ongoing")
    public OngoingMatchDto findOngoingMatchByUserId(@RequestParam Optional<UUID> userId, CurrentUser currentUser) {
        return matchService.findOngoingMatch(userId.orElse(null), currentUser);
    }

    @PostMapping("/ongoing")
    @ResponseStatus(HttpStatus.CREATED)
    public List<OngoingMatchDto> createOngoingMatch(@RequestBody List<OngoingMatchDto> request,
            CurrentUser currentUser) {
        var match = matchService.save(mapper.toModelList(request), currentUser);
        return mapper.toDto(match);
    }

    @GetMapping(value = "/stats", params = { "userId" })
    public List<MatchStatsDto> getStatsByUserId(@RequestParam UUID userId, CurrentUser currentUser) {
        return matchService.findMatchStats(userId, currentUser);
    }

}
