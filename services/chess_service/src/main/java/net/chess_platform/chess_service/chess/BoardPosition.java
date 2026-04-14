package net.chess_platform.chess_service.chess;

import net.chess_platform.chess_service.chess.piece.PieceColor;

public record BoardPosition(
        String board,
        PieceColor movingColor,
        boolean canEnPassant,
        boolean canCastle) {
}
