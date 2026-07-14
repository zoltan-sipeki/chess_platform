package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Knight;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public class KnightBehavior implements PieceBehavior {
    
    @Override
    public boolean canBeAppliedTo(Piece piece) {
        return piece instanceof Knight;
    }

    @Override
    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col) {
        var moveList = new ArrayList<Move>();

        boolean upperSecondRow = row - 2 >= 0;
        boolean upperFirstRow = row - 1 >= 0;
        boolean lowerFirstRow = row + 1 < Chessboard.SIZE;
        boolean lowerSecondRow = row + 2 < Chessboard.SIZE;
        boolean leftSecondCol = col - 2 >= 0;
        boolean leftFirstCol = col - 1 >= 0;
        boolean rightFirstCol = col + 1 < Chessboard.SIZE;
        boolean rightSecondCol = col + 2 < Chessboard.SIZE;

        if (upperSecondRow) {
            if (leftFirstCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row - 2, col - 1),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightFirstCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row - 2, col + 1),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (upperFirstRow) {
            if (leftSecondCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row - 1, col - 2),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightSecondCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row - 1, col + 2),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (lowerFirstRow) {
            if (leftSecondCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row + 1, col - 2),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightSecondCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row + 1, col + 2),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (lowerSecondRow) {
            if (leftFirstCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row + 2, col - 1),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightFirstCol) {
                var move = Move.createBasicMove(board, new Position(row, col), new Position(row + 2, col + 1),
                        piece, color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        return moveList;
    }

}
