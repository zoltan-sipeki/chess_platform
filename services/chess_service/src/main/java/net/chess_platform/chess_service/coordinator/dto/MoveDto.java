package net.chess_platform.chess_service.coordinator.dto;

public record MoveDto(

		MovedPieceDto piece,

		PositionDto from,

		PositionDto to,

		String type,

		String algebraicNotation,

		boolean isCheck,

		long timestamp,

		String promotee) {

}
