package net.chess_platform.chess_service.ws.dto;

public record MoveResultDto(
        String algebraicNotation,
        String activeColor,
        String color,
        String move,
        PositionDto from,
        PositionDto to,
        boolean promotionInProgress,
        String promotee,
        String gameOverReason,
        String winnerColor

) {

}
