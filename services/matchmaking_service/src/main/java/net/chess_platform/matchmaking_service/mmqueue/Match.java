package net.chess_platform.matchmaking_service.mmqueue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Match {

	public enum Type {
		RANKED,
		UNRANKED,
		PRIVATE
	}

	private final List<Player> players;

	private final Type matchType;

	private Map<UUID, String> matchmakingTokens;

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

	public void setMatchmakingTokens(Map<UUID, String> matchmakingTokens) {
		this.matchmakingTokens = matchmakingTokens;
	}

	public Map<UUID, String> getMatchmakingTokens() {
		return matchmakingTokens;
	}
}