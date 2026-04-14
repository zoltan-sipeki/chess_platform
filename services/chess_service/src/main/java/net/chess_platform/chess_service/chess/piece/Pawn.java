package net.chess_platform.chess_service.chess.piece;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.CaptureMove;
import net.chess_platform.chess_service.chess.move.EnPassantMove;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.move.PromotionMove;
import net.chess_platform.chess_service.chess.move.SimpleMove;
import net.chess_platform.chess_service.chess.piece.behavior.IPieceBehavior;
import net.chess_platform.chess_service.chess.piece.behavior.PawnCaptureBehavior;

public class Pawn extends AbstractPiece {

    private final int direction;

    private IPieceBehavior pawnCaptureMoves = new PawnCaptureBehavior();

    public Pawn(int row, int col, PieceColor color, Chessboard board) {
        super(row, col, color, board);
        direction = Chessboard.getPawnDirection(color);
    }

    @Override
    public List<IMove> getMoves() {
        var board = getBoard();
        var color = getColor();
        var row = getRow();
        var col = getCol();

        var moveList = new ArrayList<IMove>();
        moveList.addAll(pawnCaptureMoves.getMoves(board, color, row, col));

        if (canEnPassant(col + 1)) {
            moveList.add(new EnPassantMove(board, new Position(row, col), new Position(row + direction, col + 1),
                    new Position(row, col + 1)));
        } else if (canEnPassant(col - 1)) {
            moveList.add(new EnPassantMove(board, new Position(row, col), new Position(row + direction, col - 1),
                    new Position(row, col - 1)));
        }

        int promotionRow = Chessboard.getPromotionRow(color);

        if (hasMoved()) {
            int targetRow = row + direction;

            var target = board.getPiece(targetRow, col);
            if (target == null) {
                IMove move = new SimpleMove(board, new Position(row, col), new Position(targetRow, col));
                if (targetRow == promotionRow) {
                    move = new PromotionMove(move);
                }
                moveList.add(move);
            } else {
                IMove move = new CaptureMove(board, new Position(row, col), new Position(targetRow, col));
                if (targetRow == promotionRow) {
                    move = new PromotionMove(move);
                }
                moveList.add(move);
            }

        } else {
            int end = row + direction * 2;
            for (int i = row + direction; i != end; i += direction) {
                var target = board.getPiece(i, col);
                if (target == null) {
                    var move = new SimpleMove(board, new Position(row, col), new Position(i, col));
                    moveList.add(move);
                } else {
                    break;
                }
            }
        }

        return moveList;
    }

    private boolean canEnPassant(int targetCol) {
        var board = getBoard();
        var row = getRow();
        var color = getColor();

        int enpassantRow = Chessboard.getEnPassantRow(color);

        if (row != enpassantRow) {
            return false;
        }

        var lastMove = board.getLastMove();
        var lastMovedPiece = lastMove.getMovedPiece();
        if (!(lastMovedPiece instanceof Pawn)) {
            return false;
        }

        if (lastMovedPiece.getColor() == color) {
            return false;
        }

        var lastFrom = lastMove.getFrom();
        var lastTo = lastMove.getTo();

        if (Math.abs(lastFrom.row() - lastTo.row()) != 2) {
            return false;
        }

        return lastTo.col() == targetCol;
    }

    public boolean canEnPassant() {
        return canEnPassant(getCol() + 1) || canEnPassant(getCol() - 1);
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return getColor() == PieceColor.WHITE ? Character.toString(9817) : Character.toString(9823);
    }
}
