package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.ArrayList;
import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.move.MoveUtils;
import net.chess_platform.chess_service.chess.move.Position;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.King;
import net.chess_platform.chess_service.chess.piece.PieceColor;

public class KingBasicBehavior implements IPieceBehavior {

    @Override
    public boolean canBeAppliedTo(AbstractPiece piece) {
        return piece instanceof King;
    }

    @Override
    public List<IMove> getMoves(Chessboard board, PieceColor color, int row, int col) {
        var moveList = new ArrayList<IMove>();

        for (int i = row - 1; i <= row + 1; ++i) {
            for (int j = col - 1; j <= col + 1; ++j) {
                if (i < 0 || i >= Chessboard.SIZE || j < 0 || j >= Chessboard.SIZE || (i == row && j == col)) {
                    continue;
                }
                var move = MoveUtils.createBasicMove(board, new Position(row, col), new Position(i, j), color);
                if (move != null) {
                    moveList.add(move);
                }
            }
        }

        return moveList;
    }
}
