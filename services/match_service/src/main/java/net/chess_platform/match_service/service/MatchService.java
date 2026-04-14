package net.chess_platform.match_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.dto.MatchStatDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.exception.AccessDeniedException;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.exception.UserAlreadyInMatchException;
import net.chess_platform.match_service.mapper.MatchMapper;
import net.chess_platform.match_service.mapper.MatchStatMapper;
import net.chess_platform.match_service.model.OngoingMatch;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.MatchDetailRepository;
import net.chess_platform.match_service.repository.MatchRepository;
import net.chess_platform.match_service.repository.MatchStatRepository;
import net.chess_platform.match_service.repository.OngoingMatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    private final OngoingMatchRepository ongoingMatchRepository;

    private final MatchDetailRepository matchDetailRepository;

    private final MatchStatRepository matchStatRepository;

    private final PermissionService permissionService;

    private final MatchMapper matchMapper;

    private final MatchStatMapper matchStatMapper;

    public MatchService(MatchRepository matchRepository, OngoingMatchRepository ongoingMatchRepository,
            MatchDetailRepository matchDetailRepository, MatchStatRepository matchStatRepository,
            PermissionService permissionService, MatchMapper matchMapper, MatchStatMapper matchStatMapper) {
        this.matchRepository = matchRepository;
        this.ongoingMatchRepository = ongoingMatchRepository;
        this.matchDetailRepository = matchDetailRepository;
        this.matchStatRepository = matchStatRepository;
        this.permissionService = permissionService;
        this.matchMapper = matchMapper;
        this.matchStatMapper = matchStatMapper;
    }

    public List<MatchHistoryDto> findMatchHistory(UUID userId, Pageable pageable, CurrentUser currentUser) {
        var auth = permissionService.authorize(Action.MATCH_HISTORY_QUERY, currentUser, Map.of("userId", userId));
        var details = matchDetailRepository.findAll(auth, pageable).getContent();

        return matchMapper.toMatchHistoryList(details);
    }

    public String findReplay(UUID matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException())
                .getReplay();
    }

    @Transactional
    public List<OngoingMatch> save(List<OngoingMatch> match, CurrentUser user) {
        var auth = permissionService.authorize(Action.ONGOING_MATCH_CREATE, user, null);
        if (auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        try {
            return ongoingMatchRepository.saveAllAndFlush(match, auth);
        } catch (ConstraintViolationException e) {
            if (e.getConstraintName().endsWith("_key")) {
                throw new UserAlreadyInMatchException("One or both users are already in a match.");
            }

            throw e;
        }
    }

    public OngoingMatchDto findOngoingMatch(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.ONGOING_MATCH_QUERY, user, Map.of("userId", userId));
        var match = ongoingMatchRepository.findOne(auth).orElseThrow(() -> new EntityNotFoundException());
        return matchMapper.toDto(match);
    }

    public List<MatchStatDto> findMatchStats(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.MATCH_STATS_QUERY, user, Map.of("userId", userId));
        var stats = matchStatRepository.findAll(auth);
        return matchStatMapper.toDto(stats);
    }

}
