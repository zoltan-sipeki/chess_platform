package net.chess_platform.match_service.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.match_service.exception.MatchAlreadyExistsException;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchDetail;
import net.chess_platform.match_service.model.MatchDetail.Color;
import net.chess_platform.match_service.model.MatchDetail.Score;
import net.chess_platform.match_service.model.MatchStat;
import net.chess_platform.match_service.model.MatchUser;
import net.chess_platform.match_service.repository.MatchDetailRepository;
import net.chess_platform.match_service.repository.MatchRepository;
import net.chess_platform.match_service.repository.MatchStatRepository;
import net.chess_platform.match_service.repository.OngoingMatchRepository;
import net.chess_platform.match_service.repository.PlayerMmrRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class ChessEventService {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private DomainEventService eventService;

    private ChessEventWriter chessEventWriter;

    public ChessEventService(DomainEventService eventService, ChessEventWriter chessEventWriter) {
        this.eventService = eventService;
        this.chessEventWriter = chessEventWriter;
    }

    public void process(MatchEndedEvent e) {
        try {
            chessEventWriter.write(e);
            eventService.ack(e, SERVICE_NAME);
        } catch (MatchAlreadyExistsException ex) {
            eventService.ack(e, SERVICE_NAME);
        }
    }

    @Service
    public static class ChessEventWriter {

        private final MatchStatRepository matchStatRepository;

        private final MatchRepository matchRepository;

        private final PlayerMmrRepository matchUserRepository;

        private final MatchDetailRepository matchDetailRepository;

        private final OngoingMatchRepository ongoingMatchRepository;

        private final ObjectMapper objectMapper;

        @PersistenceContext
        private EntityManager em;

        public ChessEventWriter(MatchStatRepository matchStatRepository, MatchRepository matchRepository,
                PlayerMmrRepository matchUserRepository, MatchDetailRepository matchDetailRepository,
                OngoingMatchRepository ongoingMatchRepository, ObjectMapper objectMapper) {
            this.matchStatRepository = matchStatRepository;
            this.matchRepository = matchRepository;
            this.matchUserRepository = matchUserRepository;
            this.matchDetailRepository = matchDetailRepository;
            this.ongoingMatchRepository = ongoingMatchRepository;
            this.objectMapper = objectMapper;
        }

        @Transactional
        public void write(MatchEndedEvent e) {
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
                        matchUserRepository.updateRankedMmrByUserId(player.id(), player.mmrAfter(), m.endedAt());
                    } else if (matchType == Match.Type.UNRANKED) {
                        matchUserRepository.updateUnrankedMmrByUserId(player.id(), player.mmrAfter(), m.endedAt());
                    } else if (matchType == Match.Type.PRIVATE) {
                        matchUserRepository.updateLastPlayed(player.id(), m.endedAt());
                    }

                    var stat = matchStatRepository.findByUserIdAndMatchType(player.id(), matchType).orElse(null);
                    var score = MatchDetail.Score.valueOf(player.score());
                    var playerRef = em.getReference(MatchUser.class, player.id());

                    if (stat == null) {
                        stat = new MatchStat();
                        stat.setUser(playerRef);
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

                    var detail = new MatchDetail();
                    detail.setColor(Color.valueOf(player.color()));
                    detail.setScore(Score.valueOf(player.score()));
                    detail.setMmrBefore(player.mmrBefore());
                    detail.setMmrAfter(player.mmrAfter());
                    if (player.mmrAfter() != null && player.mmrBefore() != null) {
                        detail.setMmrChange(player.mmrAfter() - player.mmrBefore());
                    }
                    detail.setUser(playerRef);
                    detail.setMatch(match);

                    matchDetailRepository.save(detail);
                }

                ongoingMatchRepository.deleteByMatchId(m.matchId());
            } catch (JacksonException ex) {
                // should never happen
            }
        }
    }
}
