package net.chess_platform.match_service.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.exception.UserAlreadyExistsException;
import net.chess_platform.match_service.mapper.PlayerStatsMapper;
import net.chess_platform.match_service.model.Player;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.LeaderboardRepository;
import net.chess_platform.match_service.repository.LongestStreakRepository;
import net.chess_platform.match_service.repository.PlayerRepository;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

@Service
public class PlayerService {

    private final LeaderboardRepository leaderboardRepository;

    private final LongestStreakRepository longestStreakRepository;

    private final PlayerRepository playerRepository;

    private final PrivacySettingRepository privacySettingRepository;

    private final PermissionService permissionService;

    private final PlayerStatsMapper mapper;

    public PlayerService(LeaderboardRepository leaderboardRepository,
            LongestStreakRepository longestStreakRepository,
            PlayerRepository playerRepository, PrivacySettingRepository privacySettingRepository,
            PermissionService permissionService, PlayerStatsMapper mapper) {
        this.leaderboardRepository = leaderboardRepository;
        this.longestStreakRepository = longestStreakRepository;
        this.playerRepository = playerRepository;
        this.privacySettingRepository = privacySettingRepository;
        this.permissionService = permissionService;
        this.mapper = mapper;
    }

    public PlayerStatsDto findPlayerStats(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.USER_STATS_QUERY, user, Map.of("userId", userId));
        var player = playerRepository.findOne(auth).orElseThrow(() -> new EntityNotFoundException());
        var leaderboard = leaderboardRepository.findOne(auth).orElse(null);
        var longestStreak = longestStreakRepository.findAll(auth);
        return mapper.toDto(leaderboard, player, longestStreak);
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            var user = new Player();
            var data = e.getData();
            user.setId(data.userId());
            user.setDisplayName(data.displayName());
            user.setAvatar(data.avatar());

            playerRepository.saveAndFlush(user);

            var privacySettings = new ArrayList<PrivacySetting>();
            for (var setting : PrivacySetting.Resource.values()) {
                var ps = new PrivacySetting();
                ps.setPlayer(user);
                ps.setResource(setting);
                privacySettings.add(ps);
            }

            privacySettingRepository.saveAll(privacySettings);

        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }

}
