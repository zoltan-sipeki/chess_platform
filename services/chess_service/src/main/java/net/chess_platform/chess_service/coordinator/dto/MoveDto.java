package net.chess_platform.chess_service.coordinator.dto;

public record MoveDto(

		PositionDto from,

		PositionDto to,

		String type,

		String piece,

		String color,

		String checkStatus,

		PromotedPieceDto promotedPiece) {

}
