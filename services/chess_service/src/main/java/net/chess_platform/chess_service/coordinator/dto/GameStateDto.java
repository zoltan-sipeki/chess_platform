package net.chess_platform.chess_service.coordinator.dto;

import java.util.List;

public record GameStateDto(

		long nextTurn,

		String activeColor,

		boolean promotionInProgress,

		List<MoveDto> moves,

		List<PieceDto> board) {

}
