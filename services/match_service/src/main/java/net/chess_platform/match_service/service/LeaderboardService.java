package net.chess_platform.match_service.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.LeaderboardEntryDto;
import net.chess_platform.match_service.mapper.LeaderboardMapper;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.LeaderboardRepository;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    private final PermissionService permissionService;

    private final LeaderboardMapper mapper;

    public LeaderboardService(LeaderboardRepository leaderboardRepository, PermissionService permissionService,
            LeaderboardMapper mapper) {
        this.leaderboardRepository = leaderboardRepository;
        this.permissionService = permissionService;
        this.mapper = mapper;
    }

    public List<LeaderboardEntryDto> fetchLeaderboard(Pageable pageable, CurrentUser currentUser) {
        var auth = permissionService.authorize(Action.LEADERBOARD_QUERY, currentUser, null);
        var result = leaderboardRepository.findAll(auth, pageable).getContent();
        return mapper.toDtoList(result);
    }

}
