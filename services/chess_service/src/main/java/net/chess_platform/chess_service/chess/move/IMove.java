package net.chess_platform.chess_service.chess.move;

import net.chess_platform.chess_service.chess.Chessboard;
import net.chess_platform.chess_service.chess.piece.AbstractPiece;

public interface IMove {

    public boolean validate();

    public void execute();

    public void undo();

    public Chessboard getBoard();

    public AbstractPiece getMovedPiece();

    public Position getFrom();

    public Position getTo();

    public AbstractPiece getPromotee();

    public String getAlgebraicNotation();

    public void setAlgebraicNotation(String notation);
    
    public void setCheckmate();
    
    public boolean isCheck();
    
    public void setCheck();
    
    public long getTimestamp();
    
    public void setTimestamp();
}
