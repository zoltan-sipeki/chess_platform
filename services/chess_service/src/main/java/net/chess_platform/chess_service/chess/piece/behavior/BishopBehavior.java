package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveUtils;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Bishop;
import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.piece.Queen;

public class BishopBehavior implements IPieceBehavior {

    @Override
    public boolean canBeAppliedTo(AbstractPiece piece) {
        return piece instanceof Bishop || piece instanceof Queen;
    }

    @Override
    public List<IMove> getMoves(Chessboard board, PieceColor color, int row, int col) {
        var moveList = new ArrayList<IMove>();

        for (int i = row, j = col; i < Chessboard.SIZE && j < Chessboard.SIZE; ++i, ++j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int i = row, j = col; i < Chessboard.SIZE && j >= 0; ++i, --j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int i = row, j = col; i >= 0 && j >= 0; --i, --j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int i = row, j = col; i >= 0 && j < Chessboard.SIZE; --i, ++j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        return moveList;
    }
}
