package net.chess_platform.chess_service.coordinator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.coordinator.dto.MatchSnapshot;
import net.chess_platform.chess_service.coordinator.dto.MoveDto;
import net.chess_platform.chess_service.coordinator.dto.MoveResultDto;
import net.chess_platform.chess_service.coordinator.dto.PieceDto;
import net.chess_platform.chess_service.coordinator.dto.PlayerDto;
import net.chess_platform.chess_service.coordinator.dto.PositionDto;
import net.chess_platform.chess_service.coordinator.dto.PromotedPieceDto;
import net.chess_platform.chess_service.coordinator.match.Match;
import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;
import net.chess_platform.chess_service.coordinator.match.Player;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent.Payload.PromotedPiece;

@Component
public class Mapper {

    public MoveDto toDto(Move move) {
        var from = toDto(move.getFrom());
        var to = toDto(move.getTo());

        PromotedPieceDto promotedPiece = null;

        if (move instanceof PromotionMove m) {
            var p = m.getPromotedPieceInstance();
            promotedPiece = new PromotedPieceDto(p.getColor().name(), p.getType().name());
        }

        var checkStatus = move.getCheckStatus();

        return new MoveDto(from, to, move.getType().name(), move.getPiece().name(), move.getColor().name(),
                checkStatus == null ? null : checkStatus.name(), promotedPiece);
    }

    public MatchSnapshot toSnapshot(Match match) {
        var moves = new ArrayList<MoveDto>();
        for (var move : match.getMoves()) {
            moves.add(toDto(move));
        }

        var board = new ArrayList<PieceDto>();
        for (var piece : match.getBoard()) {
            if (piece == null) {
                board.add(null);
            } else {
                board.add(toDto(piece));
            }
        }

        var players = new ArrayList<MatchSnapshot.PlayerDto>();
        for (var player : match.getPlayers()) {
            players.add(new MatchSnapshot.PlayerDto(player.getId(), player.getColor().name()));
        }

        return new MatchSnapshot(match.getNextTurn(), match.getActiveColor().name(),
                match.getState().name(), players, moves, board);
    }

    public PieceDto toDto(Piece piece) {
        return new PieceDto(piece.getColor().toString(), piece.getType().name(), piece.getMoveCount());
    }

    public PlayerDto toDto(Player player) {
        var mmrBefore = player.getMmr();
        var mmrAfter = player.getNewMmr();

        return new PlayerDto(player.getId(), player.getColor().name(), mmrBefore, mmrAfter,
                player.getScore());
    }

    public List<PlayerDto> toDtoList(List<Player> players) {
        var list = new ArrayList<PlayerDto>();
        for (var player : players) {
            list.add(toDto(player));
        }
        return list;
    }

    public PositionDto toDto(Position pos) {
        return new PositionDto(pos.row(), pos.col());
    }

    public MoveResultDto toDto(MoveProcessingResult result) {
        var activeColor = result.getActiveColor();
        var move = result.getMove();
        var state = result.getState();
        var winnerColor = result.getWinnerColor();
        var scoreboard = result.getScoreboard();

        return new MoveResultDto(result.getNextTurn(), activeColor != null ? activeColor.name() : null,
                move != null ? toDto(move) : null,
                state.name(), winnerColor != null ? winnerColor.name() : null,
                scoreboard != null ? toDtoList(scoreboard) : null);
    }

    public MatchEndedEvent.Payload toEventPayload(Match match) {
        var players = new ArrayList<MatchEndedEvent.Payload.Player>();
        for (var player : match.getPlayers()) {
            players.add(toEventPayload(player, match.getType()));
        }

        var replay = new ArrayList<MatchEndedEvent.Payload.Move>();
        for (var move : match.getMoves()) {
            replay.add(toEventPayload(move));
        }

        return new MatchEndedEvent.Payload(match.getId(), match.getType().toString(),
                match.getStartedAt(), match.getEndedAt(),
                players, replay);
    }

    public MatchEndedEvent.Payload.Player toEventPayload(Player player, Match.Type matchType) {
        return new MatchEndedEvent.Payload.Player(player.getId(), player.getColor().name(), player.getMmr(),
                player.getNewMmr(),
                player.getScore());
    }

    public MatchEndedEvent.Payload.Move toEventPayload(Move move) {
        var from = toEventPayload(move.getFrom());
        var to = toEventPayload(move.getTo());

        PromotedPiece pr = null;
        if (move instanceof PromotionMove m) {
            var p = m.getPromotedPieceInstance();
            pr = new MatchEndedEvent.Payload.PromotedPiece(p.getColor().name(), p.getType().name());
        }

        var checkStatus = move.getCheckStatus();

        return new MatchEndedEvent.Payload.Move(from, to,
                move.getType().name(),
                checkStatus == null ? null : checkStatus.name(), move.getTimestamp(),
                pr);
    }

    public MatchEndedEvent.Payload.Position toEventPayload(Position pos) {
        return new MatchEndedEvent.Payload.Position(pos.row(), pos.col());
    }
}
