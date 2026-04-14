package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveUtils;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.PieceColor;
import net.chess_platform.chess_service.chess.piece.Queen;
import net.chess_platform.chess_service.chess.piece.Rook;

public class RookBehavior implements IPieceBehavior {

    @Override
    public boolean canBeAppliedTo(AbstractPiece piece) {
        return piece instanceof Rook || piece instanceof Queen;
    }

    @Override
    public List<IMove> getMoves(Chessboard board, PieceColor color, int row, int col) {
        var moveList = new ArrayList<IMove>();

        for (int j = col; j < Chessboard.SIZE; ++j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int j = col; j >= 0; --j) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(row, j), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int i = row; i < Chessboard.SIZE; ++i) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, col), color);
            if (move != null) {
                moveList.add(move);
            }
            else {
                break;
            }
        }

        for (int i = row; i >= 0; --i) {
            var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, col), color);
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
