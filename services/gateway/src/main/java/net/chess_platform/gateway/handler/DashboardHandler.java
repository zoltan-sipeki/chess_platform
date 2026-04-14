package net.chess_platform.gateway.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import net.chess_platform.gateway.integration.ChatServiceProxy;
import net.chess_platform.gateway.integration.ChatServiceProxy.ChannelDto;
import net.chess_platform.gateway.integration.ChatServiceProxy.UserDto;
import net.chess_platform.gateway.integration.MatchServiceProxy.OngoingMatchDto;
import net.chess_platform.gateway.integration.MatchServiceProxy;
import net.chess_platform.gateway.util.SecurityContextAwareAsyncSupplier;

@Component
public class DashboardHandler implements HandlerFunction<ServerResponse> {

    private final ChatServiceProxy chatService;

    private final MatchServiceProxy matchService;

    public static record DashboardDto(List<ChannelDto> channels, List<UserDto> friends, OngoingMatchDto ongoingMatch) {
    }

    public DashboardHandler(ChatServiceProxy chatService, MatchServiceProxy matchService) {
        this.chatService = chatService;
        this.matchService = matchService;
    }

    @Override
    public ServerResponse handle(ServerRequest request) throws Exception {
        var channels = CompletableFuture
                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getChannels()));

        var friends = CompletableFuture
                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getFriends()));

        var ongoingMatch = CompletableFuture
                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> matchService.findOngoingMatch()));

        CompletableFuture.allOf(channels, friends).get();

        return ServerResponse.ok()
                .body(new DashboardDto(channels.get(), friends.get(), ongoingMatch.get()));
    }
}
