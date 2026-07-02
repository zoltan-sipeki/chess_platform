package net.chess_platform.chess_service.chess;

import net.chess_platform.chess_service.chess.piece.AbstractPiece.Color;

public record BoardPosition(

		String board,

		Color movingColor,

		boolean canEnPassant,

		boolean canCastle) {
}
