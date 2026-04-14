package net.chess_platform.chess_service.ws.dto;

public record PieceDto(
        String color,
        String type,
        int moveCount,
        int row,
        int col,
        Integer direction) {

}
