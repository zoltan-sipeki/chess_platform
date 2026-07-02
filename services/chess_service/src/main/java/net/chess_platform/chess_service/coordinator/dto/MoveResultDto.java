package net.chess_platform.chess_service.coordinator.dto;

public record MoveResultDto(

		String activeColor,

		MoveDto move,

		boolean promotionInProgress,

		String gameOverReason,

		String winnerColor

) {

}
