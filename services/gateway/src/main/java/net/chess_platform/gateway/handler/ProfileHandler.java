package net.chess_platform.gateway.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import net.chess_platform.gateway.integration.ChatServiceProxy;
import net.chess_platform.gateway.integration.ChatServiceProxy.UserDto;
import net.chess_platform.gateway.integration.MatchServiceProxy;
import net.chess_platform.gateway.integration.MatchServiceProxy.MatchHistoryDto;
import net.chess_platform.gateway.integration.MatchServiceProxy.MatchStatDto;
import net.chess_platform.gateway.integration.MatchServiceProxy.PlayerStatsDto;
import net.chess_platform.gateway.integration.UserServiceProxy;
import net.chess_platform.gateway.integration.UserServiceProxy.ProfileUserDto;
import net.chess_platform.gateway.util.SecurityContextAwareAsyncSupplier;

@Component
public class ProfileHandler implements HandlerFunction<ServerResponse> {

	private final MatchServiceProxy matchService;

	private final ChatServiceProxy chatService;

	private final UserServiceProxy userService;

	public ProfileHandler(
			MatchServiceProxy matchService, ChatServiceProxy chatService, UserServiceProxy userService) {
		this.matchService = matchService;
		this.chatService = chatService;
		this.userService = userService;
	}

	public static record ProfileDto(
			ProfileUserDto user,
			String relationship,
			PlayerStatsDto playerStats,
			List<MatchHistoryDto> matches,
			List<MatchStatDto> matchStats,
			List<UserDto> friends) {
	}

	@Override
	public ServerResponse handle(ServerRequest request) throws Exception {
		var userId = request.pathVariables().get("userId");

		var user = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> userService.getUserById(userId)));

		var relationship = CompletableFuture
				.supplyAsync(
						new SecurityContextAwareAsyncSupplier<>(() -> chatService.getRelationship(userId)));

		var playerStats = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getPlayerStats(userId)));

		var matches = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getMatches(userId)));

		var matchStats = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getMatchStats(userId)));

		var friends = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getFriends(userId)));

		CompletableFuture.allOf(user, relationship, playerStats, matches, matchStats, friends).get();

		return ServerResponse.ok()
				.body(new ProfileDto(user.get(), relationship.get(), playerStats.get(), matches.get(), matchStats.get(),
						friends.get()));
	}

}
