package net.chess_platform.gateway.integration;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
public class MatchServiceProxy {

	private final RestClient restClient;

	public static record PlayerStatsDto(
			int rank,
			int mmr,
			float percentile,
			List<LongestStreakDto> longestStreaks,
			Instant joinedAt,
			OffsetDateTime lastPlayedAt) {

	}

	public static record LongestStreakDto(
			String score,
			int longestStreak) {

	}

	public static record MatchStatDto(
			UUID userId,
			String matchType,
			int gamesPlayed,
			int wins,
			int losses,
			int draws,
			float winRatio) {

	}

	public static record MatchHistoryDto(
			UUID userId,
			UUID matchId,
			String matchType,
			OffsetDateTime startedAt,
			OffsetDateTime endedAt,
			long duration,
			String color,
			String score,
			int mmrBefore,
			int mmrAfter,
			int mmrChange) {
	}

	public static record OngoingMatchDto(
			long matchId,
			UUID userId,
			String target) {

	}

	public MatchServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
		this.restClient = builder.baseUrl("http://match-service").build();
	}

	public OngoingMatchDto findOngoingMatch() {
		return restClient.get().uri("/api/matches/ongoing").retrieve().body(OngoingMatchDto.class);
	}

	public List<MatchStatDto> getMatchStats(String userId) {
		return restClient.get().uri("/api/matches/stats?userId={userId}", userId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<MatchStatDto>>() {
				});
	}

	public List<MatchHistoryDto> getMatches(String userId) {
		return restClient.get().uri("/api/matches?userId={userId}", userId)
				.retrieve()
				.body(new ParameterizedTypeReference<List<MatchHistoryDto>>() {
				});
	}

	public PlayerStatsDto getPlayerStats(String userId) {
		try {
			return restClient.get().uri("/api/players/{userId}/stats", userId)
					.retrieve()
					.body(PlayerStatsDto.class);
		} catch (HttpClientErrorException.NotFound e) {
			return null;
		}
	}

}
