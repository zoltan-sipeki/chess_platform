package net.chess_platform.chess_service.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Bishop;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.Knight;
import net.chess_platform.chess_service.chess.piece.Pawn;
import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.piece.PieceType;
import net.chess_platform.chess_service.chess.piece.Queen;
import net.chess_platform.chess_service.chess.piece.Rook;
import net.chess_platform.chess_service.chess.piece.behavior.BishopBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KingBasicBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.KnightBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PawnCaptureBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.RookBehavior;
import net.chess_platform.chess_service.chess.pojo.MoveDetails;
import net.chess_platform.chess_service.chess.pojo.MoveResult;
import net.chess_platform.chess_service.chess.pojo.PromotionDetails;

public class Chessboard {

    public enum GameOverReason {
        CHECKMATE,
        DEAD_POSITION,
        THREEFOLD_REPETITION,
        FIFTY_MOVE_RULE,
        FLAG_FALL,
        STALEMATE,
        RESIGNATION
    }

    public static final int SIZE = 8;

    public static final int LEFT_ROOK_COL = 0;

    public static final int RIGHT_ROOK_COL = 7;

    private static final PieceType[] PIECE_TYPES = PieceType.values();

    private AbstractPiece[] board = new AbstractPiece[SIZE * SIZE];

    private List<IMove> moves = new ArrayList<>();

    private Map<BoardPosition, Integer> threefoldRepetitionTracker = new HashMap<>();

    private PieceColor activeColor = PieceColor.WHITE;

    private boolean promotionInProgress = false;

    private int fiftyMoveCounter = 0;

    private AbstractPiece whiteKing;

    private AbstractPiece blackKing;

    public Chessboard() {
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (i == 1) {
                    board[i * SIZE + j] = createPiece(i, j, PieceType.PAWN, PieceColor.BLACK);
                } else if (i == 6) {
                    board[i * SIZE + j] = createPiece(i, j, PieceType.PAWN, PieceColor.WHITE);
                } else if (i > 1 && i < 6) {
                    board[i * SIZE + j] = null;
                }
            }
        }

        board[0 * SIZE + 0] = createPiece(0, 0, PieceType.ROOK, PieceColor.BLACK);
        board[0 * SIZE + 1] = createPiece(0, 1, PieceType.KNIGHT, PieceColor.BLACK);
        board[0 * SIZE + 2] = createPiece(0, 2, PieceType.BISHOP, PieceColor.BLACK);
        board[0 * SIZE + 3] = createPiece(0, 3, PieceType.QUEEN, PieceColor.BLACK);
        blackKing = createPiece(0, 4, PieceType.KING, PieceColor.BLACK);
        board[0 * SIZE + 4] = blackKing;
        board[0 * SIZE + 5] = createPiece(0, 5, PieceType.BISHOP, PieceColor.BLACK);
        board[0 * SIZE + 6] = createPiece(0, 6, PieceType.KNIGHT, PieceColor.BLACK);
        board[0 * SIZE + 7] = createPiece(0, 7, PieceType.ROOK, PieceColor.BLACK);

        board[7 * SIZE + 0] = createPiece(7, 0, PieceType.ROOK, PieceColor.WHITE);
        board[7 * SIZE + 1] = createPiece(7, 1, PieceType.KNIGHT, PieceColor.WHITE);
        board[7 * SIZE + 2] = createPiece(7, 2, PieceType.BISHOP, PieceColor.WHITE);
        board[7 * SIZE + 3] = createPiece(7, 3, PieceType.QUEEN, PieceColor.WHITE);
        whiteKing = createPiece(7, 4, PieceType.KING, PieceColor.WHITE);
        board[7 * SIZE + 4] = whiteKing;
        board[7 * SIZE + 5] = createPiece(7, 5, PieceType.BISHOP, PieceColor.WHITE);
        board[7 * SIZE + 6] = createPiece(7, 6, PieceType.KNIGHT, PieceColor.WHITE);
        board[7 * SIZE + 7] = createPiece(7, 7, PieceType.ROOK, PieceColor.WHITE);
    }

    public AbstractPiece[] getBoard() {
        return board;
    }

    public PieceColor getActiveColor() {
        return activeColor;
    }

    public List<IMove> getMoves() {
        return moves;
    }

    public MoveResult makeMove(MoveDetails moveDetails) {
        if (activeColor != moveDetails.color()) {
            return new MoveResult(activeColor);
        }

        if (promotionInProgress) {
            return new MoveResult(activeColor);
        }

        var from = moveDetails.from();
        var to = moveDetails.to();
        if (from.row() < 0 || from.col() >= SIZE || to.row() < 0 || to.col() >= SIZE) {
            return new MoveResult(activeColor);
        }

        var piece = getPiece(from);
        var move = piece.getMove(to);

        if (move == null) {
            return new MoveResult(activeColor);
        }

        if (!move.validate()) {
            return new MoveResult(activeColor);
        }

        move.execute();
        move.setTimestamp();
        moves.add(move);

        return evaluateMove(move);
    }

    private MoveResult evaluateMove(IMove move) {
        var reason = checkEndConditions(move);
        PieceColor winnerColor = null;
        if (reason == null) {
            activeColor = getOpponentColor(activeColor);
        } else if (isDraw(reason)) {
            winnerColor = PieceColor.NONE;
        } else {
            winnerColor = activeColor;
        }

        return new MoveResult(move, activeColor, reason, winnerColor, promotionInProgress);
    }

    public static boolean isDraw(GameOverReason reason) {
        return reason == GameOverReason.THREEFOLD_REPETITION || reason == GameOverReason.FIFTY_MOVE_RULE
                || reason == GameOverReason.DEAD_POSITION || reason == GameOverReason.STALEMATE;
    }

    private GameOverReason checkEndConditions(IMove move) {
        var opponentColor = getOpponentColor(activeColor);

        boolean check = isKingInCheck(opponentColor);
        boolean canMove = canMove(opponentColor);

        if (check) {
            if (canMove) {
                move.setCheck();
            } else {
                move.setCheckmate();
                return GameOverReason.CHECKMATE;
            }
        } else if (!canMove) {
            return GameOverReason.STALEMATE;
        }

        if (checkThreefoldRepetition(opponentColor)) {
            return GameOverReason.THREEFOLD_REPETITION;
        }
        if (checkFiftyMoveRule()) {
            return GameOverReason.FIFTY_MOVE_RULE;
        }

        if (checkDeadPosition()) {
            return GameOverReason.DEAD_POSITION;
        }

        return null;
    }

    public MoveResult makeMove(PromotionDetails message) {
        if (activeColor != message.color()) {
            return new MoveResult(activeColor);
        }

        if (!promotionInProgress) {
            return new MoveResult(activeColor);
        }

        var moveResult = promote(message.promotee());
        return moveResult;
    }

    public MoveResult flagFall() {
        return new MoveResult(null, activeColor, GameOverReason.FLAG_FALL, getOpponentColor(activeColor));
    }

    public MoveResult resign(PieceColor color) {
        if (activeColor == color) {
            return new MoveResult(null, activeColor, GameOverReason.RESIGNATION, getOpponentColor(activeColor));
        }

        return new MoveResult(null, activeColor, GameOverReason.RESIGNATION, activeColor);
    }

    public MoveResult promoteRandomly() {
        return promote(getRandomPieceType());
    }

    private MoveResult promote(PieceType promotee) {
        var move = (PromotionMove) moves.getLast();
        var to = move.getTo();
        move.setPromotee(createPiece(to.row(), to.col(), promotee, activeColor));
        move.execute();

        return evaluateMove(move);
    }

    private PieceType getRandomPieceType() {
        return PIECE_TYPES[ThreadLocalRandom.current().nextInt(PIECE_TYPES.length)];
    }

    private static PieceColor getOpponentColor(PieceColor color) {
        return color == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }

    private boolean checkThreefoldRepetition(PieceColor opponentColor) {
        var canEnPassant = canEnPassant();
        var canCastle = canCastle();
        var boardPosition = new BoardPosition(stringifyBoard(), opponentColor, canEnPassant, canCastle);
        int occurrence = threefoldRepetitionTracker.get(boardPosition);
        if (occurrence == 2) {
            return true;
        }

        threefoldRepetitionTracker.put(boardPosition, occurrence + 1);
        return false;
    }

    private boolean checkFiftyMoveRule() {
        if (promotionInProgress) {
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
                || lastMove.getMovedPiece() instanceof Pawn || secondLastMove.getMovedPiece() instanceof Pawn) {
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
            if (piece.getColor() == PieceColor.WHITE) {
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

        AbstractPiece blackKnight = null;
        AbstractPiece blackBishop = null;
        AbstractPiece whiteKnight = null;
        AbstractPiece whiteBishop = null;

        for (var piece : board) {
            if (piece.getColor() == PieceColor.WHITE) {
                if (piece instanceof Knight) {
                    whiteKnight = piece;
                } else if (piece instanceof Bishop) {
                    whiteBishop = piece;
                }
            } else {
                if (piece instanceof Knight) {
                    blackKnight = piece;
                } else if (piece instanceof Bishop) {
                    blackBishop = piece;
                }
            }
        }

        if (whiteCount == 2 && blackCount == 2) {
            return whiteBishop != null && blackBishop != null && hasSameColorSquare(whiteBishop, blackBishop);
        }

        return blackBishop != null || blackKnight != null || whiteBishop != null || whiteKnight != null;
    }

    private static boolean hasSameColorSquare(AbstractPiece p1, AbstractPiece p2) {
        boolean p1parity = (p1.getRow() & 1) == (p1.getCol() & 1);
        boolean p2parity = (p2.getRow() & 1) == (p2.getCol() & 1);

        return p1parity == p2parity;
    }

    public static int getPawnDirection(PieceColor color) {
        return color == PieceColor.WHITE ? -1 : 1;
    }

    private boolean canMove(PieceColor color) {
        for (var piece : board) {
            if (piece.getColor() == color && piece.canMove()) {
                return true;
            }
        }

        return false;
    }

    private boolean canEnPassant() {
        for (var piece : board) {
            if (piece instanceof Pawn pawn && pawn.canEnPassant()) {
                return true;
            }
        }

        return false;
    }

    private boolean canCastle() {
        for (var piece : board) {
            if (piece instanceof King king && king.canCastle()) {
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

    public static int getPromotionRow(PieceColor color) {
        return color == PieceColor.WHITE ? 0 : SIZE;
    }

    public static int getEnPassantRow(PieceColor color) {
        return color == PieceColor.WHITE ? 3 : 4;
    }

    public AbstractPiece createPiece(int row, int col, PieceType type, PieceColor color) {
        return switch (type) {
            case PAWN -> new Pawn(row, col, color, this);
            case KNIGHT -> new Knight(row, col, color, this);
            case BISHOP -> new Bishop(row, col, color, this);
            case ROOK -> new Rook(row, col, color, this);
            case QUEEN -> new Queen(row, col, color, this);
            case KING -> new King(row, col, color, this);
        };
    }

    public AbstractPiece getPiece(int row, int col) {
        return board[row * SIZE + col];
    }

    public AbstractPiece getPiece(Position pos) {
        return board[pos.row() * SIZE + pos.col()];
    }

    public void setPiece(AbstractPiece piece, int row, int col) {
        board[row * SIZE + col] = piece;
        if (piece != null) {
            piece.setRow(row);
            piece.setCol(col);
        }
    }

    public void setPiece(AbstractPiece piece, Position pos) {
        board[pos.row() * SIZE + pos.col()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
    }

    public void setPromotionInProgress(boolean val) {
        this.promotionInProgress = val;
    }

    public IMove getLastMove() {
        return moves.getLast();
    }

    public boolean isKingInCheck(PieceColor color) {
        if (color == PieceColor.WHITE) {
            return isUnderAttack(whiteKing.getRow(), whiteKing.getCol(), color);
        }

        return isUnderAttack(blackKing.getRow(), blackKing.getCol(), color);
    }

    public boolean isUnderAttack(int row, int col, PieceColor color) {
        var behaviors = List.of(new PawnCaptureBehavior(), new KnightBehavior(), new KingBasicBehavior(),
                new RookBehavior(), new BishopBehavior());
        for (var behavior : behaviors) {
            if (isAttacked(row, col, color, behavior)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAttacked(int row, int col, PieceColor color, IPieceBehavior behavior) {
        var moveList = behavior.getMoves(this, color, row, col);
        for (var move : moveList) {
            if (!(move instanceof CaptureMove m)) {
                continue;
            }
            var capturedPiece = m.getCapturedPiece();
            if (capturedPiece.getColor() != color
                    && behavior.canBeAppliedTo(capturedPiece)) {
                return true;
            }
        }
        return false;
    }

}
