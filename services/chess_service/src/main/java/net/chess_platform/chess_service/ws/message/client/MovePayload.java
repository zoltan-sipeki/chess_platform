package net.chess_platform.chess_service.ws.message.client;

import net.chess_platform.chess_service.chess.move.Position;

public record MovePayload(

        long matchId,

        Position from,

        Position to) {
}
