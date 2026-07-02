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
import net.chess_platform.chess_service.coordinator.dto.GameStateDto;
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

    // public MatchResultDto.MoveDto toMatchResult(IMove move) {
    // var from = toMatchResult(move.getFrom());
    // var to = toMatchResult(move.getTo());

    // var movedPiece = move.getMovedPiece();
    // var movedPieceDto = new
    // MatchResultDto.PieceDto(movedPiece.getColor().toString(),
    // toPieceType(movedPiece).toString());
    // String promotee = null;
    // if (move instanceof PromotionMove m) {
    // promotee = toPieceType(m.getPromotee()).toString();
    // }
    // return new MatchResultDto.MoveDto(from, to, movedPieceDto,
    // toMoveType(move).toString(),
    // move.getAlgebraicNotation(),
    // move.isCheck(), move.getTimestamp(), promotee);
    // }

    public GameStateDto toGameStateDto(Match match) {
        var moves = new ArrayList<MoveDto>();
        var chessboard = match.getChessboard();
        for (var move : chessboard.getMoves()) {
            moves.add(toDto(move));
        }

        var board = new ArrayList<PieceDto>();
        for (var piece : chessboard.getBoard()) {
            if (piece == null) {
                board.add(null);
            } else {
                board.add(toDto(piece));
            }
        }

        return new GameStateDto(match.getNextTurn(), chessboard.getActiveColor().name(),
                chessboard.isPromotionInProgress(), moves, board);
    }

    public PieceDto toDto(AbstractPiece piece) {
        return new PieceDto(piece.getColor().toString(), piece.getType().name(), piece.getMoveCount(),
                piece.getRow(), piece.getCol(), piece instanceof Pawn p ? p.getDirection() : null);
    }

    // public MatchResultDto toMatchResult(Match match) {
    // var players = new ArrayList<MatchResultDto.PlayerDto>();
    // for (var player : match.getPlayers()) {
    // players.add(toMatchResult(player, match.getType()));
    // }

    // var chessboard = match.getChessboard();
    // var replay = new ArrayList<MatchResultDto.MoveDto>();
    // for (var move : chessboard.getMoves()) {
    // replay.add(toMatchResult(move));
    // }

    // return new MatchResultDto(match.getId(), match.getType().toString(),
    // match.getStartedAt(), match.getEndedAt(),
    // players, replay);
    // }

    // public MatchResultDto.PlayerDto toMatchResult(Player player, Match.Type
    // matchType) {
    // Integer mmrBefore = matchType == Match.Type.PRIVATE ? null : player.getMmr();
    // Integer mmrAfter = matchType == Match.Type.PRIVATE ? null :
    // player.getNewMmr();

    // return new MatchResultDto.PlayerDto(player.getId(),
    // player.getColor().toString(), mmrBefore, mmrAfter,
    // toScore(player.getScore()).toString());
    // }

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

    // public MatchResultDto.PositionDto toMatchResult(Position pos) {
    // return new MatchResultDto.PositionDto(pos.row(), pos.col());
    // }

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
}
