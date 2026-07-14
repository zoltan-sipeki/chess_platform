package net.chess_platform.chess_service.ws.message.client;

import net.chess_platform.chess_service.chess.move.Position;

public class MovePayload implements MatchPayload {

	private Position from;

	private Position to;

	public MovePayload() {}

	public MovePayload(Position from, Position to) {
		this.from = from;
		this.to = to;
	}

	public Position getFrom() {
		return from;
	}

	public Position getTo() {
		return to;
	}
}