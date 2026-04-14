package net.chess_platform.gateway.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import net.chess_platform.gateway.integration.ChatServiceProxy;
import net.chess_platform.gateway.integration.ChatServiceProxy.UserDto;
import net.chess_platform.gateway.integration.MatchServiceProxy;
import net.chess_platform.gateway.integration.MatchServiceProxy.MatchHistoryDto;
import net.chess_platform.gateway.integration.MatchServiceProxy.MatchStatDto;
import net.chess_platform.gateway.integration.MatchServiceProxy.PlayerMMrDto;
import net.chess_platform.gateway.util.SecurityContextAwareAsyncSupplier;

@Component
public class ProfileHandler implements HandlerFunction<ServerResponse> {

	private final MatchServiceProxy matchService;

	private final ChatServiceProxy chatService;

	public ProfileHandler(
			MatchServiceProxy matchService, ChatServiceProxy chatService) {
		this.matchService = matchService;
		this.chatService = chatService;
	}

	public static record ProfileDto(PlayerMMrDto ratings, List<MatchHistoryDto> matches, List<MatchStatDto> stats,
			List<UserDto> friends) {
	}

	@Override
	public ServerResponse handle(ServerRequest request) throws Exception {
		var userId = request.pathVariables().get("userId");

		var ratings = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getRatings(userId)));

		var matches = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getMatches(userId)));

		var stats = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.getStats(userId)));

		var friends = CompletableFuture
				.supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getFriends(userId)));

		CompletableFuture.allOf(ratings, matches, stats, friends).get();

		return ServerResponse.ok()
				.body(new ProfileDto(ratings.get(), matches.get(), stats.get(), friends.get()));
	}

}
