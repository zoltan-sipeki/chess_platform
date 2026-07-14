package net.chess_platform.chess_service.coordinator.dto;

import java.util.List;

public record MoveResultDto(

		long nextTurn,

		String activeColor,

		MoveDto move,

		String state,

		String winnerColor,

		List<PlayerDto> scoreboard

) {

}
