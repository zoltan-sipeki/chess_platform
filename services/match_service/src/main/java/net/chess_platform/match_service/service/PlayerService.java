package net.chess_platform.match_service.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.mapper.PlayerStatsMapper;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.LeaderboardRepository;
import net.chess_platform.match_service.repository.LongestStreakRepository;
import net.chess_platform.match_service.repository.PlayerMmrRepository;

@Service
public class PlayerService {

    private final LeaderboardRepository leaderboardRepository;

    private final LongestStreakRepository longestStreakRepository;

    private final PlayerMmrRepository playerMmrRepository;

    private final PermissionService permissionService;

    private final PlayerStatsMapper mapper;

    public PlayerService(LeaderboardRepository leaderboardRepository, LongestStreakRepository longestStreakRepository,
            PlayerMmrRepository playerMmrRepository, PermissionService permissionService, PlayerStatsMapper mapper) {
        this.leaderboardRepository = leaderboardRepository;
        this.longestStreakRepository = longestStreakRepository;
        this.playerMmrRepository = playerMmrRepository;
        this.permissionService = permissionService;
        this.mapper = mapper;
    }

    public PlayerStatsDto findPlayerStats(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.USER_STATS_QUERY, user, Map.of("userId", userId));
        var info = playerMmrRepository.findOne(auth).orElseThrow(() -> new EntityNotFoundException());
        var leaderboard = leaderboardRepository.findOne(auth).orElse(null);
        var longestStreak = longestStreakRepository.findAll(auth);
        return mapper.toDto(leaderboard, info, longestStreak);
    }
}
