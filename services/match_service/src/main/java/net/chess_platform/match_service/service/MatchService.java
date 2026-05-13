package net.chess_platform.match_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchHistoryDto;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.dto.OngoingMatchDto;
import net.chess_platform.match_service.exception.AccessDeniedException;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.exception.MatchAlreadyExistsException;
import net.chess_platform.match_service.exception.UserAlreadyInMatchException;
import net.chess_platform.match_service.mapper.MatchMapper;
import net.chess_platform.match_service.mapper.MatchStatMapper;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchResult;
import net.chess_platform.match_service.model.MatchResult.Color;
import net.chess_platform.match_service.model.MatchResult.Score;
import net.chess_platform.match_service.model.MatchStat;
import net.chess_platform.match_service.model.OngoingMatch;
import net.chess_platform.match_service.model.Player;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.MatchDetailRepository;
import net.chess_platform.match_service.repository.MatchRepository;
import net.chess_platform.match_service.repository.MatchStatRepository;
import net.chess_platform.match_service.repository.OngoingMatchRepository;
import net.chess_platform.match_service.repository.PlayerRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    private final OngoingMatchRepository ongoingMatchRepository;

    private final MatchDetailRepository matchDetailRepository;

    private final MatchStatRepository matchStatRepository;

    private final PlayerRepository playerRepository;

    private final PermissionService permissionService;

    private final MatchMapper matchMapper;

    private final MatchStatMapper matchStatMapper;

    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager em;

    public MatchService(MatchRepository matchRepository, OngoingMatchRepository ongoingMatchRepository,
            MatchDetailRepository matchDetailRepository, MatchStatRepository matchStatRepository,
            PlayerRepository playerRepository,
            PermissionService permissionService, MatchMapper matchMapper, MatchStatMapper matchStatMapper,
            ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.ongoingMatchRepository = ongoingMatchRepository;
        this.matchDetailRepository = matchDetailRepository;
        this.matchStatRepository = matchStatRepository;
        this.playerRepository = playerRepository;
        this.permissionService = permissionService;
        this.matchMapper = matchMapper;
        this.matchStatMapper = matchStatMapper;
        this.objectMapper = objectMapper;
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

    public List<MatchStatsDto> findMatchStats(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.MATCH_STATS_QUERY, user, Map.of("userId", userId));
        var stats = matchStatRepository.findAll(auth);
        return matchStatMapper.toDto(stats);
    }

    @Transactional
    public void process(MatchEndedEvent e) {
        try {
            var m = e.getData();
            var matchType = Match.Type.valueOf(m.matchType());

            var match = new Match();
            match.setId(m.matchUuid());
            match.setDuration(m.endedAt().toEpochSecond() - m.startedAt().toEpochSecond());
            match.setEndedAt(m.endedAt());
            match.setStartedAt(m.startedAt());

            var replay = objectMapper.writeValueAsString(m.replay());
            match.setReplay(replay);
            match.setType(matchType);
            try {
                matchRepository.save(match);
            } catch (ConstraintViolationException ex) {
                throw new MatchAlreadyExistsException();
            }

            for (var player : m.players()) {
                if (matchType == Match.Type.RANKED) {
                    playerRepository.updateRankedMmrByUserId(player.id(), player.mmrAfter(), m.endedAt());
                } else if (matchType == Match.Type.UNRANKED) {
                    playerRepository.updateUnrankedMmrByUserId(player.id(), player.mmrAfter(), m.endedAt());
                } else if (matchType == Match.Type.PRIVATE) {
                    playerRepository.updateLastPlayed(player.id(), m.endedAt());
                }

                var stat = matchStatRepository.findByPlayerIdAndMatchType(player.id(), matchType).orElse(null);
                var score = MatchResult.Score.valueOf(player.score());
                var playerRef = em.getReference(Player.class, player.id());

                if (stat == null) {
                    stat = new MatchStat();
                    stat.setPlayer(playerRef);
                    stat.setMatchType(matchType);
                    stat.setWins(score == Score.WIN ? 1 : 0);
                    stat.setLosses(score == Score.LOSS ? 1 : 0);
                    stat.setDraws(score == Score.DRAW ? 1 : 0);
                    stat.setGamesPlayed(1);
                    stat.setWinRatio(stat.getWins());
                    matchStatRepository.save(stat);
                } else {
                    int wins = stat.getWins();
                    int losses = stat.getLosses();
                    int draws = stat.getDraws();

                    stat.setWins(score == Score.WIN ? wins + 1 : wins);
                    stat.setLosses(score == Score.LOSS ? losses + 1 : losses);
                    stat.setDraws(score == Score.DRAW ? draws + 1 : draws);
                    stat.setGamesPlayed(stat.getGamesPlayed() + 1);
                    stat.setWinRatio((float) stat.getWins() / stat.getGamesPlayed());
                    matchStatRepository.save(stat);
                }

                var detail = new MatchResult();
                detail.setColor(Color.valueOf(player.color()));
                detail.setScore(Score.valueOf(player.score()));
                detail.setMmrBefore(player.mmrBefore());
                detail.setMmrAfter(player.mmrAfter());
                if (player.mmrAfter() != null && player.mmrBefore() != null) {
                    detail.setMmrChange(player.mmrAfter() - player.mmrBefore());
                }
                detail.setPlayer(playerRef);
                detail.setMatch(match);

                matchDetailRepository.save(detail);
            }

            ongoingMatchRepository.deleteByMatchId(m.matchId());
        } catch (JacksonException ex) {
            // should never happen
        }
    }

}
