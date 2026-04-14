package net.chess_platform.chess_service.ws.dto;

import java.util.List;

public record GameStateDto(
        long nextTurn,
        List<MoveDto> moves,
        List<PieceDto> board,
        String activeColor) {

}
