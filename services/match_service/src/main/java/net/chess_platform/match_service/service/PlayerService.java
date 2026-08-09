package net.chess_platform.match_service.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.authorization.PlayerAuthorizationService;
import net.chess_platform.match_service.dto.PlayerStatsDto;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.mapper.PlayerMapper;
import net.chess_platform.match_service.mapper.PlayerStatsMapper;
import net.chess_platform.match_service.model.Player;
import net.chess_platform.match_service.model.PrivacySetting;
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

    private final PlayerStatsMapper playerStatsMapper;

    private final PlayerMapper playerMapper;

    private final PlayerAuthorizationService authService;

    private final DomainEventService eventService;

    public PlayerService(LeaderboardRepository leaderboardRepository,
            LongestStreakRepository longestStreakRepository,
            PlayerRepository playerRepository, PrivacySettingRepository privacySettingRepository,
            PlayerStatsMapper mapper, PlayerMapper playerMapper,
            PlayerAuthorizationService authService, DomainEventService eventService) {
        this.leaderboardRepository = leaderboardRepository;
        this.longestStreakRepository = longestStreakRepository;
        this.playerRepository = playerRepository;
        this.privacySettingRepository = privacySettingRepository;
        this.playerStatsMapper = mapper;
        this.playerMapper = playerMapper;
        this.authService = authService;
        this.eventService = eventService;
    }

    public PlayerStatsDto findPlayerStats(UUID userId, CurrentUser user) {
        var auth = authService.authorizePlayerStatsQuery(user, userId);
        if (!auth.isAllowed()) {
            throw new EntityNotFoundException();
        }

        var player = playerRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException());
        var leaderboard = leaderboardRepository.findByPlayerId(userId).orElseThrow(() -> new EntityNotFoundException());
        var longestStreak = longestStreakRepository.findAllByPlayerId(userId);

        return playerStatsMapper.toDto(leaderboard, player, longestStreak);
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            if (eventService.exists(e)) {
                return;
            }

            var user = new Player();
            var data = e.getData();
            user.setId(data.id());
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

            eventService.ack(e);

        } catch (DataIntegrityViolationException ex) {
            eventService.ack(e);
        }
    }

    @Transactional
    public void process(UserUpdatedEvent e) {
        if (eventService.exists(e)) {
            return;
        }

        var u = e.getData();
        var updates = playerMapper.toUpdate(u);

        playerRepository.update(u.id(), updates);

        eventService.ack(e);
    }

}
