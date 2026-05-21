package net.chess_platform.gateway.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import net.chess_platform.gateway.integration.ChatServiceProxy;
import net.chess_platform.gateway.integration.ChatServiceProxy.ChannelDto;
import net.chess_platform.gateway.integration.ChatServiceProxy.FriendListDto;
import net.chess_platform.gateway.integration.MatchServiceProxy;
import net.chess_platform.gateway.integration.MatchServiceProxy.OngoingMatchDto;
import net.chess_platform.gateway.integration.UserServiceProxy;
import net.chess_platform.gateway.integration.UserServiceProxy.ProfileUserDto;
import net.chess_platform.gateway.util.SecurityContextAwareAsyncSupplier;

@Component
public class DashboardHandler implements HandlerFunction<ServerResponse> {

        private final ChatServiceProxy chatService;

        private final MatchServiceProxy matchService;

        private final UserServiceProxy userService;

        public static record DashboardDto(ProfileUserDto user, List<ChannelDto> channels, FriendListDto friends,
                        OngoingMatchDto ongoingMatch) {
        }

        public DashboardHandler(ChatServiceProxy chatService, MatchServiceProxy matchService,
                        UserServiceProxy userService) {
                this.chatService = chatService;
                this.matchService = matchService;
                this.userService = userService;
        }

        @Override
        public ServerResponse handle(ServerRequest request) throws Exception {
                var user = CompletableFuture
                                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(
                                                () -> userService.getCurrentUser()));

                var channels = CompletableFuture
                                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getChannels()));

                var friends = CompletableFuture
                                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(() -> chatService.getFriends()));

                var ongoingMatch = CompletableFuture
                                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(
                                                () -> matchService.findOngoingMatch()));

                CompletableFuture.allOf(user, channels, friends).get();

                return ServerResponse.ok()
                                .body(new DashboardDto(user.get(), channels.get(), friends.get(), ongoingMatch.get()));
        }
}
