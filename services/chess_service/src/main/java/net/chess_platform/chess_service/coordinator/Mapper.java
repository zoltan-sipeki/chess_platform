package net.chess_platform.chess_service.coordinator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.CastlingMove;
import net.chess_platform.chess_service.chess.move.EnPassantMove;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveType;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.move.SimpleMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Bishop;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Knight;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.piece.PieceType;
import net.chess_platform.chess_service.chess.piece.Queen;
import net.chess_platform.chess_service.chess.piece.Rook;
import net.chess_platform.chess_service.chess.pojo.MoveResult;
import net.chess_platform.chess_service.coordinator.match.Match;
import net.chess_platform.chess_service.coordinator.match.MoveProcessingResult;
import net.chess_platform.chess_service.coordinator.match.Player;
import net.chess_platform.chess_service.ws.dto.GameStateDto;
import net.chess_platform.chess_service.ws.dto.MoveDto;
import net.chess_platform.chess_service.ws.dto.MoveProcessingResultDto;
import net.chess_platform.chess_service.ws.dto.MoveResultDto;
import net.chess_platform.chess_service.ws.dto.MovedPieceDto;
import net.chess_platform.chess_service.ws.dto.PieceDto;
import net.chess_platform.chess_service.ws.dto.PlayerDto;
import net.chess_platform.chess_service.ws.dto.PositionDto;
import net.chess_platform.common.dto.chess.MatchResultDto;

@Component
public class Mapper {

    public static PieceType toPieceType(AbstractPiece piece) {
        return switch (piece) {
            case Bishop p -> PieceType.BISHOP;
            case King p -> PieceType.KING;
            case Knight p -> PieceType.KNIGHT;
            case Pawn p -> PieceType.PAWN;
            case Queen p -> PieceType.QUEEN;
            case Rook p -> PieceType.ROOK;
            default -> null;
        };
    }

    public static MoveType toMoveType(IMove move) {
        return switch (move) {
            case SimpleMove m -> MoveType.SIMPLE;
            case CaptureMove m -> MoveType.CAPTURE;
            case CastlingMove m -> MoveType.CASTLING;
            case EnPassantMove m -> MoveType.EN_PASSANT;
            case PromotionMove m -> toMoveType(m.getMove());
            default -> null;
        };
    }

    private static Match.Score toScore(float score) {
        if (score == 1) {
            return Match.Score.WIN;
        }

        if (score == 0.5) {
            return Match.Score.DRAW;
        }

        return Match.Score.LOSS;

    }

    public MoveDto toDto(IMove move) {
        var from = toDto(move.getFrom());
        var to = toDto(move.getTo());

        var movedPiece = move.getMovedPiece();
        var movedPieceDto = new MovedPieceDto(movedPiece.getColor().toString(), toPieceType(movedPiece).toString());
        String promotee = null;
        if (move instanceof PromotionMove m) {
            promotee = toPieceType(m.getPromotee()).toString();
        }
        return new MoveDto(from, to, movedPieceDto, toMoveType(move).toString(), move.getAlgebraicNotation(),
                move.isCheck(), move.getTimestamp(), promotee);
    }

    public MatchResultDto.MoveDto toMatchResult(IMove move) {
        var from = toMatchResult(move.getFrom());
        var to = toMatchResult(move.getTo());

        var movedPiece = move.getMovedPiece();
        var movedPieceDto = new MatchResultDto.PieceDto(movedPiece.getColor().toString(),
                toPieceType(movedPiece).toString());
        String promotee = null;
        if (move instanceof PromotionMove m) {
            promotee = toPieceType(m.getPromotee()).toString();
        }
        return new MatchResultDto.MoveDto(from, to, movedPieceDto, toMoveType(move).toString(),
                move.getAlgebraicNotation(),
                move.isCheck(), move.getTimestamp(), promotee);
    }

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

        return new GameStateDto(match.getNextTurn(), moves, board, chessboard.getActiveColor().toString());
    }

    public PieceDto toDto(AbstractPiece piece) {
        return new PieceDto(piece.getColor().toString(), toPieceType(piece).toString(), piece.getMoveCount(),
                piece.getRow(), piece.getCol(), piece instanceof Pawn p ? p.getDirection() : null);
    }

    public MatchResultDto toMatchResult(Match match) {
        var players = new ArrayList<MatchResultDto.PlayerDto>();
        for (var player : match.getPlayers()) {
            players.add(toMatchResult(player, match.getType()));
        }

        var chessboard = match.getChessboard();
        var replay = new ArrayList<MatchResultDto.MoveDto>();
        for (var move : chessboard.getMoves()) {
            replay.add(toMatchResult(move));
        }

        return new MatchResultDto(match.getId(), match.getType().toString(), match.getStartedAt(), match.getEndedAt(),
                players, replay);
    }

    public MatchResultDto.PlayerDto toMatchResult(Player player, Match.Type matchType) {
        Integer mmrBefore = matchType == Match.Type.PRIVATE ? null : player.getMmr();
        Integer mmrAfter = matchType == Match.Type.PRIVATE ? null : player.getNewMmr();

        return new MatchResultDto.PlayerDto(player.getId(), player.getColor().toString(), mmrBefore, mmrAfter,
                toScore(player.getScore()).toString());
    }

    public PlayerDto toDto(Player player) {
        var mmrBefore = player.getMmr();
        var mmrAfter = player.getNewMmr();

        return new PlayerDto(player.getId(), player.getColor().toString(), mmrBefore, mmrAfter,
                toScore(player.getScore()).toString());
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

    public MatchResultDto.PositionDto toMatchResult(Position pos) {
        return new MatchResultDto.PositionDto(pos.row(), pos.col());
    }

    public MoveResultDto toDto(MoveResult result) {
        var activeColor = result.getActiveColor();
        var color = result.getColor();
        var move = result.getMove();
        var promotee = result.getPromotee();
        var gameOverReason = result.getGameOverReason();
        var winnerColor = result.getWinnerColor();
        var from = result.getFrom();
        var to = result.getTo();

        return new MoveResultDto(result.getAlgebraicNotation(),
                activeColor == null ? null : activeColor.toString(),
                color == null ? null : color.toString(), move == null ? null : move.toString(),
                from == null ? null : toDto(from),
                to == null ? null : toDto(to), result.isPromotionInProgress(),
                promotee == null ? null : promotee.toString(),
                gameOverReason == null ? null : gameOverReason.toString(),
                winnerColor == null ? null : winnerColor.toString());
    }

    public MoveProcessingResultDto toDto(MoveProcessingResult result) {
        return new MoveProcessingResultDto(result.getNextTurn(), toDto(result.getMoveResult()),
                toDtoList(result.getScoreboard()));
    }
}
