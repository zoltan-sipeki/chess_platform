package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveUtils;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Knight;
import net.chess_platform.chess_service.chess.piece.PieceColor;

public class KnightBehavior implements IPieceBehavior {
    
    @Override
    public boolean canBeAppliedTo(AbstractPiece piece) {
        return piece instanceof Knight;
    }

    @Override
    public List<IMove> getMoves(Chessboard board, PieceColor color, int row, int col) {
        var moveList = new ArrayList<IMove>();

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
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row - 2, col - 1),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightFirstCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row - 2, col + 1),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (upperFirstRow) {
            if (leftSecondCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row - 1, col - 2),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightSecondCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row - 1, col + 2),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (lowerFirstRow) {
            if (leftSecondCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row + 1, col - 2),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightSecondCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row + 1, col + 2),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        if (lowerSecondRow) {
            if (leftFirstCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row + 2, col - 1),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
            if (rightFirstCol) {
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row + 2, col + 1),
                        color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        return moveList;
    }

}
