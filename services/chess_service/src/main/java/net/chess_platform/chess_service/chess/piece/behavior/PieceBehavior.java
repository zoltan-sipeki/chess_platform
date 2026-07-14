package net.chess_platform.chess_service.chess.piece.behavior;

import java.util.List;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.move.Move;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;
import net.chess_platform.chess_service.chess.piece.Piece;
import net.chess_platform.chess_service.chess.piece.Piece.Color;

public interface PieceBehavior {

    public List<Move> getMoves(Chessboard board, AbstractPiece.Type piece, Color color, int row, int col);

    public boolean canBeAppliedTo(Piece piece);
}
