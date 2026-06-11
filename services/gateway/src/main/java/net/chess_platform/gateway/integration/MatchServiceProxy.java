package net.chess_platform.gateway.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MatchServiceProxy {

	private final RestClient restClient;

	public static record PrivacyDto(
			String matchHistory,
			String matchStats,
			String playerStats) {
	}

	public MatchServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
		this.restClient = builder.baseUrl("http://match-service").build();
	}

	public PrivacyDto getPrivacySettings() {
		return restClient.get().uri("/api/privacy").retrieve().body(PrivacyDto.class);
	}

}
