package net.chess_platform.matchmaking_service.mmqueue;

import java.util.List;

import net.chess_platform.matchmaking_service.model.Player;

public class Match {

	public enum Type {
		RANKED,
		UNRANKED,
		PRIVATE
	}

	private final List<Player> players;

	private final Type matchType;

	public Match(List<Player> players, Type matchType) {
		this.players = players;
		this.matchType = matchType;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Type getMatchType() {
		return matchType;
	}
}