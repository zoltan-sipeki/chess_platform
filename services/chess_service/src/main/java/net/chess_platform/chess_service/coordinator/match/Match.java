package net.chess_platform.chess_service.coordinator.match;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.pojo.MoveDetails;
import net.chess_platform.chess_service.chess.pojo.MoveResult;
import net.chess_platform.chess_service.chess.pojo.PromotionDetails;
import net.chess_platform.chess_service.coordinator.EloRatingCalculator;
import net.chess_platform.chess_service.coordinator.match.event.FlagFallEvent;
import net.chess_platform.chess_service.coordinator.match.event.PromotionTimeoutEvent;
import net.chess_platform.chess_service.ws.message.client.MovePayload;
import net.chess_platform.chess_service.ws.message.client.PromotionPayload;
import net.chess_platform.chess_service.ws.message.client.ResignPayload;

public class Match {

    public enum Type {
        RANKED,
        UNRANKED,
        PRIVATE
    }

    public enum Score {
        WIN,
        LOSS,
        DRAW
    }

    private static final long FLAG_FALL_TIMEOUT_MS = 10000;

    private static final long PROMOTION_TIMEOUT_MS = 30 * 1000;

    private static final PieceColor[] COLORS = PieceColor.values();

    private long id;

    private Type type;

    private List<Player> players = new ArrayList<>();

    private Chessboard chessboard;

    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?> timeout;

    private Consumer<Object> eventQueue;

    private EloRatingCalculator eloRatingService = new EloRatingCalculator();

    private long nextTurn;

    private OffsetDateTime startedAt;

    private OffsetDateTime endedAt;

    private long duration;

    public Match(long id, Type type, ScheduledExecutorService scheduler, Consumer<Object> eventQueue) {
        this.id = id;
        this.type = type;
        this.scheduler = scheduler;
        this.eventQueue = eventQueue;
    }

    public boolean addPlayer(Player player) {
        if (players.size() == 2) {
            return false;
        }

        if (findPlayer(player.getId()) != null) {
            return false;
        }

        players.add(player);
        return true;
    }

    public Player findPlayer(UUID userId) {
        for (var p : players) {
            if (p.getId().equals(userId)) {
                return p;
            }
        }
        return null;
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public Player getPlayerOtherThan(UUID userId) {
        for (var player : players) {
            if (!player.getId().equals(userId)) {
                return player;
            }
        }
        return null;
    }

    public void start() {
        chessboard = new Chessboard();
        setPlayerColors();
        setStartedAt();
        startFlagFallTimer();
    }

    public boolean isEveryBodyConnected() {
        return players.size() == 2;
    }

    public MoveProcessingResult process(MovePayload message) {
        var player = findPlayer(message.getUserId());
        MoveDetails moveDetails = new MoveDetails(player.getColor(), message.getFrom(), message.getTo());

        var moveResult = chessboard.makeMove(moveDetails);

        return process(moveResult);
    }

    public MoveProcessingResult process(PromotionPayload message) {
        var player = findPlayer(message.getUserId());
        var promotionDetails = new PromotionDetails(player.getColor(), message.getPromotee());

        var moveResult = chessboard.makeMove(promotionDetails);

        return process(moveResult);
    }

    public MoveProcessingResult process(ResignPayload message) {
        var player = findPlayer(message.getUserId());
        var moveResult = chessboard.resign(player.getColor());

        return process(moveResult);
    }

    private MoveProcessingResult process(MoveResult moveResult) {
        if (moveResult.isGameOver()) {
            timeout.cancel(true);
            setNextTurn(0);
            setEndedAt();
            calculatePlayerScores(moveResult);
            return new MoveProcessingResult(moveResult, players);
        }

        if (moveResult.isPromotionInProgress()) {
            setNextTurn(FLAG_FALL_TIMEOUT_MS);
            startPromotionTimer();
        } else if (!moveResult.isInvalid()) {
            setNextTurn(PROMOTION_TIMEOUT_MS);
            startFlagFallTimer();
        }

        return new MoveProcessingResult(moveResult, nextTurn);
    }

    private void calculatePlayerScores(MoveResult moveResult) {
        var winnerIndex = getPlayerIndexByColor(moveResult.getWinnerColor());
        var winner = players.get(winnerIndex);
        var loser = players.get(players.size() - 1 - winnerIndex);

        if (moveResult.isDraw()) {
            winner.setScore(0.5f);
            loser.setScore(0.5f);
        } else {
            winner.setScore(1f);
            loser.setScore(0f);
        }

        if (type == Type.PRIVATE) {
            return;
        }

        var newMmrs = eloRatingService.calculateMmrs(winner, loser);
        winner.setNewMmr(newMmrs[0]);
        loser.setNewMmr(newMmrs[1]);
    }

    private int getPlayerIndexByColor(PieceColor color) {
        if (color == null) {
            return -1;
        }

        if (color == PieceColor.NONE) {
            return 0;
        }

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getColor() == color) {
                return i;
            }
        }

        return -1;
    }

    private void setPlayerColors() {
        var colorInt = ThreadLocalRandom.current().nextInt(0, 2);
        players.get(0).setColor(COLORS[colorInt]);
        players.get(1).setColor(COLORS[1 - colorInt]);
    }

    private void startFlagFallTimer() {
        if (timeout != null) {
            timeout.cancel(true);
        }
        timeout = scheduler.schedule(() -> {
            eventQueue.accept(new Runnable() {
                @Override
                public void run() {
                    var result = process(chessboard.flagFall());
                    var event = new FlagFallEvent(id, result);
                    eventQueue.accept(event);
                }
            });
        }, FLAG_FALL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    private void startPromotionTimer() {
        if (timeout != null) {
            timeout.cancel(true);
        }
        timeout = scheduler.schedule(() -> {
            eventQueue.accept(new Runnable() {
                @Override
                public void run() {
                    var result = process(chessboard.promoteRandomly());
                    var event = new PromotionTimeoutEvent(id, result);
                    eventQueue.accept(event);
                }
            });
        }, PROMOTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public long getNextTurn() {
        return nextTurn;
    }

    public long getDuration() {
        return duration;
    }

    private void setStartedAt() {
        startedAt = OffsetDateTime.now();
    }

    private void setNextTurn(long duration) {
        nextTurn = OffsetDateTime.now().toInstant().plusMillis(duration).toEpochMilli();
    }

    public boolean hasStarted() {
        return startedAt != null;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    private void setEndedAt() {
        endedAt = OffsetDateTime.now();
    }
}
