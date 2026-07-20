package net.chess_platform.match_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.MatchHistoryListDto;
import net.chess_platform.match_service.dto.MatchHistorySearchParams;
import net.chess_platform.match_service.dto.MatchStatsDto;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.mapper.MatchMapper;
import net.chess_platform.match_service.mapper.MatchStatMapper;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchResult;
import net.chess_platform.match_service.model.MatchResult.Color;
import net.chess_platform.match_service.model.MatchResult.Outcome;
import net.chess_platform.match_service.model.MatchStat;
import net.chess_platform.match_service.model.Player;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.MatchRepository;
import net.chess_platform.match_service.repository.MatchResultRepository;
import net.chess_platform.match_service.repository.MatchStatRepository;
import net.chess_platform.match_service.repository.PlayerRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    private final MatchResultRepository matchDetailRepository;

    private final MatchStatRepository matchStatRepository;

    private final PlayerRepository playerRepository;

    private final PermissionService permissionService;

    private final MatchMapper matchMapper;

    private final MatchStatMapper matchStatMapper;

    private final ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager em;

    public MatchService(MatchRepository matchRepository,
            MatchResultRepository matchDetailRepository, MatchStatRepository matchStatRepository,
            PlayerRepository playerRepository,
            PermissionService permissionService, MatchMapper matchMapper, MatchStatMapper matchStatMapper,
            ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.matchDetailRepository = matchDetailRepository;
        this.matchStatRepository = matchStatRepository;
        this.playerRepository = playerRepository;
        this.permissionService = permissionService;
        this.matchMapper = matchMapper;
        this.matchStatMapper = matchStatMapper;
        this.objectMapper = objectMapper;
    }

    public MatchHistoryListDto findMatchHistory(UUID userId, MatchHistorySearchParams searchParams, Pageable pageable,
            CurrentUser currentUser) {
        var auth = permissionService.authorize(Action.MATCH_HISTORY_QUERY, currentUser, Map.of("userId", userId));

        Specification<MatchResult> spec = Specification.unrestricted();
        if (searchParams.outcome() != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("outcome"), searchParams.outcome()));
        }

        if (searchParams.matchType() != null) {
            spec = spec.and(
                    (root, cq, cb) -> cb.equal(root.get("match").get("type"),
                            searchParams.matchType()));
        }

        var page = matchDetailRepository.findAll(auth, spec, pageable);

        return new MatchHistoryListDto(page.getTotalElements(), matchMapper.toMatchHistoryList(page.getContent()));
    }

    public String findReplay(UUID matchId) {
        return matchRepository.findById(matchId).orElseThrow(() -> new EntityNotFoundException())
                .getReplay();
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
            match.setDuration(m.endedAt().toEpochMilli() - m.startedAt().toEpochMilli());
            match.setEndedAt(m.endedAt());
            match.setStartedAt(m.startedAt());

            var replay = objectMapper.writeValueAsString(e.getData());
            match.setReplay(replay);
            match.setType(matchType);
            matchRepository.save(match);

            for (var player : m.players()) {

                var update = new Player.Update();
                if (matchType == Match.Type.RANKED) {
                    update.setRankedMmr(player.mmrAfter());
                } else if (matchType == Match.Type.UNRANKED) {
                    update.setUnrankedMmr(player.mmrAfter());
                }

                update.setLastPlayedAt(match.getStartedAt());

                playerRepository.update(player.id(), update);

                var stat = matchStatRepository.findByPlayerIdAndMatchType(player.id(), matchType).orElse(null);
                var outcome = toOutcome(player.score());
                var playerRef = em.getReference(Player.class, player.id());

                if (stat == null) {
                    stat = new MatchStat();
                    stat.setPlayer(playerRef);
                    stat.setMatchType(matchType);
                    stat.setWins(outcome == Outcome.WIN ? 1 : 0);
                    stat.setLosses(outcome == Outcome.LOSS ? 1 : 0);
                    stat.setDraws(outcome == Outcome.DRAW ? 1 : 0);
                    stat.setGamesPlayed(1);
                    stat.setWinRatio(stat.getWins());
                    matchStatRepository.save(stat);
                } else {
                    int wins = stat.getWins();
                    int losses = stat.getLosses();
                    int draws = stat.getDraws();

                    stat.setWins(outcome == Outcome.WIN ? wins + 1 : wins);
                    stat.setLosses(outcome == Outcome.LOSS ? losses + 1 : losses);
                    stat.setDraws(outcome == Outcome.DRAW ? draws + 1 : draws);
                    stat.setGamesPlayed(stat.getGamesPlayed() + 1);
                    stat.setWinRatio((float) stat.getWins() / stat.getGamesPlayed());
                    matchStatRepository.save(stat);
                }

                var detail = new MatchResult();
                detail.setColor(Color.valueOf(player.color()));
                detail.setOutcome(outcome);
                detail.setMmrBefore(player.mmrBefore());
                detail.setMmrAfter(player.mmrAfter());
                if (player.mmrAfter() != null && player.mmrBefore() != null) {
                    detail.setMmrChange(player.mmrAfter() - player.mmrBefore());
                }
                detail.setPlayer(playerRef);
                detail.setMatch(match);

                matchDetailRepository.save(detail);
            }

        } catch (JacksonException ex) {
            // should never happen
        }
    }

    private Outcome toOutcome(float score) {
        if (score == 1.0f) {
            return Outcome.WIN;
        } else if (score == 0.0f) {
            return Outcome.LOSS;
        } else if (score == 0.5f) {
            return Outcome.DRAW;
        }

        return null;
    }

}
