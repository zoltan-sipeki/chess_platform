package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.IMove;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.AbstractPiece.Color;

public interface IPieceBehavior {

    public List<IMove> getMoves(Chessboard board, Color color, int row, int col);

    public boolean canBeAppliedTo(AbstractPiece piece);
}
