package net.chess_platform.chess_service.coordinator.dto;

import java.util.List;
import java.util.UUID;

public record MatchSnapshot(

		long nextTurn,

		String activeColor,

		String state,

		List<PlayerDto> players,

		List<MoveDto> moves,

		List<PieceDto> board) {

	public static record PlayerDto(UUID id, String color) {
	}

}
