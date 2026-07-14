package net.chess_platform.chess_service.chess.piece;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.EnPassantMove;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.SimpleMove;
import net.chess_platform.chess_service.chess.piece.behavior.PawnCaptureBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PieceBehavior;

public class Pawn extends AbstractPiece {

    private final PieceBehavior pawnCaptureMoves = new PawnCaptureBehavior();

    public Pawn(Color color) {
        super(color, Type.PAWN);
    }

    @Override
    public List<Move> getMoves(Chessboard board, int row, int col) {
        var type = getType();
        var color = getColor();

        var moveList = new ArrayList<Move>();
        moveList.addAll(pawnCaptureMoves.getMoves(board, type, color, row, col));

        int direction = Chessboard.getPawnDirection(color);

        if (canEnPassant(board, row, col, col + 1)) {
            moveList.add(
                    new EnPassantMove(type, color, new Position(row, col), new Position(row + direction, col + 1)));
        } else if (canEnPassant(board, row, col, col - 1)) {
            moveList.add(
                    new EnPassantMove(type, color, new Position(row, col), new Position(row + direction, col - 1)));
        }

        if (hasMoved()) {
            int targetRow = row + direction;

            var target = board.getPiece(targetRow, col);
            if (target == null) {
                var move = new SimpleMove(type, color, new Position(row, col), new Position(targetRow, col));
                moveList.add(move);

            } else {
                var move = new CaptureMove(type, color, new Position(row, col), new Position(targetRow, col));
                moveList.add(move);
            }

        } else {
            int end = row + direction * 2;
            for (int i = row + direction;; i += direction) {
                if (direction < 0 ? i < end : i > end) {
                    break;
                }

                var target = board.getPiece(i, col);
                if (target == null) {
                    var move = new SimpleMove(type, color, new Position(row, col), new Position(i, col));
                    moveList.add(move);
                } else {
                    break;
                }
            }
        }

        return moveList;
    }

    private boolean canEnPassant(Chessboard board, int row, int col, int targetCol) {
        var color = getColor();

        int enpassantRow = Chessboard.getEnPassantRow(color);

        if (row != enpassantRow) {
            return false;
        }

        var lastMove = board.getLastMove();
        if (lastMove.getPiece() != Type.PAWN) {
            return false;
        }

        if (lastMove.getColor() == color) {
            return false;
        }

        var lastFrom = lastMove.getFrom();
        var lastTo = lastMove.getTo();

        if (Math.abs(lastFrom.row() - lastTo.row()) != 2) {
            return false;
        }

        return lastTo.col() == targetCol;
    }

    public boolean canEnPassant(Chessboard board, int row, int col) {
        return canEnPassant(board, row, col, col + 1) || canEnPassant(board, row, col, col - 1);
    }

    @Override
    public String toString() {
        return getColor() == Color.WHITE ? Character.toString(9817) : Character.toString(9823);
    }
}
