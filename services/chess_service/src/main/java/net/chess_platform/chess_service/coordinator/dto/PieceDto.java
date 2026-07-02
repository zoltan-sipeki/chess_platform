package net.chess_platform.chess_service.coordinator.dto;

public record PieceDto(

		String color,

		String type,

		int moveCount,

		int row,

		int col,

		Integer direction) {

}
