package net.chess_platform.chess_service.coordinator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.pojo.MoveResult;
import net.chess_platform.chess_service.coordinator.dto.MatchSnapshot;
import net.chess_platform.chess_service.coordinator.dto.MoveDto;
import net.chess_platform.chess_service.coordinator.dto.MoveProcessingResultDto;
import net.chess_platform.chess_service.coordinator.dto.MoveResultDto;
import net.chess_platform.chess_service.coordinator.dto.MovedPieceDto;
import net.chess_platform.chess_service.coordinator.dto.PieceDto;
import net.chess_platform.chess_service.coordinator.dto.PlayerDto;
import net.chess_platform.chess_service.coordinator.dto.PositionDto;
import net.chess_platform.chess_service.coordinator.match.Match;
import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;
import net.chess_platform.chess_service.coordinator.match.Player;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;

@Component
public class Mapper {

    public MoveDto toDto(IMove move) {
        var from = toDto(move.getFrom());
        var to = toDto(move.getTo());

        var piece = move.getMovedPiece();
        var pieceDto = new MovedPieceDto(piece.getColor().name(), piece.getType().name());
        String promotee = null;
        if (move instanceof PromotionMove m) {
            promotee = m.getPromotee().getType().name();
        }
        return new MoveDto(pieceDto, from, to, move.getType().name(), move.getAlgebraicNotation(),
                move.isCheck(), move.getTimestamp(), promotee);
    }

    public MatchSnapshot toGameStateDto(Match match) {
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

        return new MatchSnapshot(match.getNextTurn(), match.getActiveColor().name(),
                match.isPromotionInProgress(), moves, board);
    }

    public PieceDto toDto(AbstractPiece piece) {
        return new PieceDto(piece.getColor().toString(), piece.getType().name(), piece.getMoveCount(),
                piece.getRow(), piece.getCol(), piece instanceof Pawn p ? p.getDirection() : null);
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

    public MoveResultDto toDto(MoveResult result) {
        var activeColor = result.getActiveColor();
        var move = result.getMove();
        boolean promotionInProgress = result.isPromotionInProgress();
        var gameOverReason = result.getGameOverReason();
        var winnerColor = result.getWinnerColor();

        return new MoveResultDto(activeColor != null ? activeColor.name() : null, toDto(move), promotionInProgress,
                gameOverReason != null ? gameOverReason.name() : null,
                winnerColor != null ? winnerColor.name() : null);
    }

    public MoveProcessingResultDto toDto(MoveProcessingResult result) {
        return new MoveProcessingResultDto(result.getNextTurn(), toDto(result.getMoveResult()),
                toDtoList(result.getScoreboard()));
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

    public MatchEndedEvent.Payload.Move toEventPayload(IMove move) {
        var from = toEventPayload(move.getFrom());
        var to = toEventPayload(move.getTo());

        var movedPiece = move.getMovedPiece();
        var movedPieceDto = new MatchEndedEvent.Payload.Piece(movedPiece.getColor().name(),
                movedPiece.getType().name());
        String promotee = null;
        if (move instanceof PromotionMove m) {
            promotee = m.getPromotee().getType().name();
        }
        return new MatchEndedEvent.Payload.Move(from, to, movedPieceDto,
                move.getType().name(),
                move.getAlgebraicNotation(),
                move.isCheck(), move.getTimestamp(), promotee);
    }

    public MatchEndedEvent.Payload.Position toEventPayload(Position pos) {
        return new MatchEndedEvent.Payload.Position(pos.row(), pos.col());
    }
}
