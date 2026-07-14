package net.chess_platform.chess_service.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.chess_platform.chess_service.chess.move.AbstractMove;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Move.CheckStatus;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.piece.Bishop;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Knight;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;
import net.chess_platform.chess_service.chess.piece.Piece.Type;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KingBasicBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KnightBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PawnCaptureBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.RookBehavior;
import net.chess_platform.chess_service.chess.pojo.MoveDetails;
import net.chess_platform.chess_service.chess.pojo.MoveResult;
import net.chess_platform.chess_service.chess.pojo.PromotionDetails;

public class Chessboard {

    public enum State {
        ACTIVE,
        AWAITING_PROMOTION,
        CHECKMATE,
        STALEMATE,
        DEAD_POSITION,
        THREEFOLD_REPETITION,
        FIFTY_MOVE_RULE,
        FLAG_FALL,
        RESIGNATION
    }

    public static final int SIZE = 8;

    public static final int LEFT_ROOK_COL = 0;

    public static final int RIGHT_ROOK_COL = 7;

    private static final Type[] PIECE_TYPES = Type.values();

    private Piece[] board = new Piece[SIZE * SIZE];

    private List<Move> moves = new ArrayList<>();

    private Map<String, Integer> threefoldRepetitionTracker = new HashMap<>();

    private Color activeColor = Color.WHITE;

    private State state = State.ACTIVE;

    private int fiftyMoveCounter = 0;

    public Chessboard() {
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (i == 1) {
                    board[i * SIZE + j] = Piece.create(Type.PAWN, Color.BLACK);
                } else if (i == 6) {
                    board[i * SIZE + j] = Piece.create(Type.PAWN, Color.WHITE);
                } else if (i > 1 && i < 6) {
                    board[i * SIZE + j] = null;
                }
            }
        }

        board[0 * SIZE + 0] = Piece.create(Type.ROOK, Color.BLACK);
        board[0 * SIZE + 1] = Piece.create(Type.KNIGHT, Color.BLACK);
        board[0 * SIZE + 2] = Piece.create(Type.BISHOP, Color.BLACK);
        board[0 * SIZE + 3] = Piece.create(Type.QUEEN, Color.BLACK);
        board[0 * SIZE + 4] = Piece.create(Type.KING, Color.BLACK);
        board[0 * SIZE + 5] = Piece.create(Type.BISHOP, Color.BLACK);
        board[0 * SIZE + 6] = Piece.create(Type.KNIGHT, Color.BLACK);
        board[0 * SIZE + 7] = Piece.create(Type.ROOK, Color.BLACK);

        board[7 * SIZE + 0] = Piece.create(Type.ROOK, Color.WHITE);
        board[7 * SIZE + 1] = Piece.create(Type.KNIGHT, Color.WHITE);
        board[7 * SIZE + 2] = Piece.create(Type.BISHOP, Color.WHITE);
        board[7 * SIZE + 3] = Piece.create(Type.QUEEN, Color.WHITE);
        board[7 * SIZE + 4] = Piece.create(Type.KING, Color.WHITE);
        board[7 * SIZE + 5] = Piece.create(Type.BISHOP, Color.WHITE);
        board[7 * SIZE + 6] = Piece.create(Type.KNIGHT, Color.WHITE);
        board[7 * SIZE + 7] = Piece.create(Type.ROOK, Color.WHITE);
    }

    public Piece[] getBoard() {
        return board;
    }

    public Color getActiveColor() {
        return activeColor;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public MoveResult makeMove(MoveDetails moveDetails) {
        if (state != State.ACTIVE) {
            return new MoveResult(null, activeColor, state);
        }

        if (activeColor != moveDetails.color()) {
            return new MoveResult(null, activeColor, state);
        }

        var from = moveDetails.from();
        var to = moveDetails.to();
        if (from.row() < 0 || from.col() >= SIZE || to.row() < 0 || to.col() >= SIZE) {
            return new MoveResult(null, activeColor, state);
        }

        var piece = getPiece(from);
        if (piece == null || piece.getColor() != activeColor) {
            return new MoveResult(null, activeColor, state);
        }

        var move = piece.getMove(to, this, from.row(), from.col());

        if (move == null) {
            return new MoveResult(null, activeColor, state);
        }

        if (!move.validate(this)) {
            return new MoveResult(null, activeColor, state);
        }

        move.execute(this);
        moves.add(move);

        if (move.isPromotion()) {
            state = State.AWAITING_PROMOTION;
            return new MoveResult(move, activeColor, state);
        }

        return evaluateMove(move);
    }

    private MoveResult evaluateMove(Move move) {
        state = checkEndConditions(move);
        if (state == State.ACTIVE) {
            activeColor = getOpponentColor(activeColor);
            return new MoveResult(move, activeColor, state);
        }

        if (isDraw(state)) {
            return new MoveResult(move, state, null);
        }

        return new MoveResult(move, state, activeColor);
    }

    public static boolean isDraw(State reason) {
        return reason == State.THREEFOLD_REPETITION || reason == State.FIFTY_MOVE_RULE
                || reason == State.DEAD_POSITION || reason == State.STALEMATE;
    }

    private State checkEndConditions(Move move) {
        var opponentColor = getOpponentColor(activeColor);

        boolean check = isKingInCheck(opponentColor);
        boolean canMove = canMove(opponentColor);

        if (check) {
            if (canMove) {
                move.setCheckStatus(CheckStatus.CHECK);
            } else {
                move.setCheckStatus(CheckStatus.CHECKMATE);
                return State.CHECKMATE;
            }
        } else if (!canMove) {
            return State.STALEMATE;
        }

        if (checkThreefoldRepetition(opponentColor)) {
            return State.THREEFOLD_REPETITION;
        }
        if (checkFiftyMoveRule()) {
            return State.FIFTY_MOVE_RULE;
        }

        if (checkDeadPosition()) {
            return State.DEAD_POSITION;
        }

        return State.ACTIVE;
    }

    public MoveResult makeMove(PromotionDetails details) {
        if (state != State.AWAITING_PROMOTION) {
            return new MoveResult(null, activeColor, state);
        }

        if (activeColor != details.color()) {
            return new MoveResult(null, activeColor, state);
        }

        var moveResult = promote(details.promotedPiece());

        return moveResult;
    }

    public MoveResult flagFall() {
        return new MoveResult(null, State.FLAG_FALL, getOpponentColor(activeColor));
    }

    public MoveResult resign(Color color) {
        if (activeColor == color) {
            return new MoveResult(null, State.RESIGNATION, getOpponentColor(activeColor));
        }

        return new MoveResult(null, State.RESIGNATION, activeColor);
    }

    public MoveResult promoteRandomly() {
        return promote(getRandomType());
    }

    public State getState() {
        return state;
    }

    private MoveResult promote(Type promotedPieceType) {
        var lastMove = moves.removeLast();
        var move = new PromotionMove((AbstractMove) lastMove, Piece.create(promotedPieceType, activeColor));
        move.execute(this);
        moves.add(move);
        return evaluateMove(move);
    }

    private Type getRandomType() {
        return PIECE_TYPES[ThreadLocalRandom.current().nextInt(PIECE_TYPES.length)];
    }

    private static Color getOpponentColor(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private boolean checkThreefoldRepetition(Color opponentColor) {
        var canEnPassant = canEnPassant();
        var canCastle = canCastle();
        var boardPosition = new BoardPosition(stringifyBoard(), opponentColor, canEnPassant, canCastle).toString();
        int occurrence = threefoldRepetitionTracker.getOrDefault(boardPosition, 0);
        if (occurrence == 2) {
            return true;
        }

        threefoldRepetitionTracker.put(boardPosition, occurrence + 1);
        return false;
    }

    private boolean checkFiftyMoveRule() {
        if (state == State.AWAITING_PROMOTION) {
            return false;
        }

        int moveCount = moves.size();
        if ((moveCount % 1) == 1) {
            return false;
        }

        if (moves.size() < 2) {
            return false;
        }

        var lastMove = moves.getLast();
        var secondLastMove = moves.get(moves.size() - 2);

        if (lastMove instanceof CaptureMove || secondLastMove instanceof CaptureMove
                || lastMove.getPiece() == Type.PAWN
                || secondLastMove.getPiece() == Type.PAWN) {
            fiftyMoveCounter = 0;
        } else if (++fiftyMoveCounter == 50) {
            return true;
        }

        return false;
    }

    private boolean checkDeadPosition() {
        int whiteCount = 0;
        int blackCount = 0;
        for (var piece : board) {
            if (piece == null) {
                continue;
            }

            if (piece.getColor() == Color.WHITE) {
                ++whiteCount;
            } else {
                ++blackCount;
            }
        }

        if (whiteCount > 2 || blackCount > 2) {
            return false;
        }

        if (whiteCount == 1 && blackCount == 1) {
            return true;
        }

        Position blackKnight = null;
        Position blackBishop = null;
        Position whiteKnight = null;
        Position whiteBishop = null;

        for (int i = 0; i < board.length; ++i) {
            var piece = board[i];

            if (piece == null) {
                continue;
            }

            var pos = new Position(i / SIZE, i % SIZE);

            if (piece.getColor() == Color.WHITE) {
                if (piece instanceof Knight) {
                    whiteKnight = pos;
                } else if (piece instanceof Bishop) {
                    whiteBishop = pos;
                }
            } else {
                if (piece instanceof Knight) {
                    blackKnight = pos;
                } else if (piece instanceof Bishop) {
                    blackBishop = pos;
                }
            }
        }

        if (whiteCount == 2 && blackCount == 2) {
            return whiteBishop != null && blackBishop != null && hasSameColorSquare(whiteBishop, blackBishop);
        }

        return blackBishop != null || blackKnight != null || whiteBishop != null || whiteKnight != null;
    }

    private static boolean hasSameColorSquare(Position p1, Position p2) {
        boolean p1parity = (p1.row() & 1) == (p1.col() & 1);
        boolean p2parity = (p2.row() & 1) == (p2.col() & 1);

        return p1parity == p2parity;
    }

    public static int getPawnDirection(Color color) {
        return color == Color.WHITE ? -1 : 1;
    }

    private boolean canMove(Color color) {
        for (int i = 0; i < board.length; ++i) {
            var piece = board[i];
            if (piece != null && piece.getColor() == color && piece.canMove(this, i / SIZE, i % SIZE)) {
                return true;
            }
        }

        return false;
    }

    private boolean canEnPassant() {
        for (int i = 0; i < board.length; ++i) {
            var piece = board[i];
            if (piece instanceof Pawn pawn && pawn.canEnPassant(this, i / SIZE, i % SIZE)) {
                return true;
            }
        }

        return false;
    }

    private boolean canCastle() {
        for (int i = 0; i < board.length; ++i) {
            var piece = board[i];
            if (piece instanceof King king && king.canCastle(this, i / SIZE, i % SIZE)) {
                return true;
            }
        }

        return false;
    }

    private String stringifyBoard() {
        var builder = new StringBuilder();

        for (var piece : board) {
            if (piece == null) {
                builder.append("_");
            } else {
                builder.append(piece.toString());
            }
        }

        return builder.toString();
    }

    public static int getPromotionRow(Color color) {
        return color == Color.WHITE ? 0 : SIZE - 1;
    }

    public static int getEnPassantRow(Color color) {
        return color == Color.WHITE ? 3 : 4;
    }

    public Piece getPiece(int row, int col) {
        return board[row * SIZE + col];
    }

    public Piece getPiece(Position pos) {
        return board[pos.row() * SIZE + pos.col()];
    }

    public void setPiece(Piece piece, int row, int col) {
        board[row * SIZE + col] = piece;
    }

    public void setPiece(Piece piece, Position pos) {
        board[pos.row() * SIZE + pos.col()] = piece;
    }

    public Move getLastMove() {
        return moves.getLast();
    }

    public boolean isKingInCheck(Color color) {
        var pos = findKing(color);
        return isUnderAttack(pos.row(), pos.col(), color);
    }

    public boolean isUnderAttack(int row, int col, Color color) {
        var behaviors = List.of(new PawnCaptureBehavior(), new KnightBehavior(), new KingBasicBehavior(),
                new RookBehavior(), new BishopBehavior());
        for (var behavior : behaviors) {
            if (isAttacked(row, col, color, behavior)) {
                return true;
            }
        }

        return false;
    }

    private Position findKing(Color color) {
        for (int i = 0; i < board.length; ++i) {
            var piece = board[i];
            if (piece instanceof King king && king.getColor() == color) {
                return new Position(i / SIZE, i % SIZE);
            }
        }

        return null;
    }

    private boolean isAttacked(int row, int col, Color color, PieceBehavior behavior) {
        var moveList = behavior.getMoves(this, null, color, row, col);
        for (var move : moveList) {
            if (!(move instanceof CaptureMove m)) {
                continue;
            }

            var capturedPiece = this.getPiece(m.getTo());
            if (capturedPiece.getColor() != color
                    && behavior.canBeAppliedTo(capturedPiece)) {
                return true;
            }
        }
        return false;
    }

}
