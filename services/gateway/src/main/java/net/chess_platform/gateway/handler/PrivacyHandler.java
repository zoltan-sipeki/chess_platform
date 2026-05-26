package net.chess_platform.gateway.handler;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import net.chess_platform.gateway.integration.ChatServiceProxy;
import net.chess_platform.gateway.integration.MatchServiceProxy;
import net.chess_platform.gateway.util.SecurityContextAwareAsyncSupplier;

@Component
public class PrivacyHandler implements HandlerFunction<ServerResponse> {

    public static record PrivacyDto(String friends, String matchHistory, String matchStats, String playerStats) {
    }

    private final MatchServiceProxy matchService;

    private final ChatServiceProxy chatService;

    public PrivacyHandler(MatchServiceProxy matchService, ChatServiceProxy chatService) {
        this.matchService = matchService;
        this.chatService = chatService;
    }

    @Override
    public ServerResponse handle(ServerRequest request) throws Exception {

        var matchPrivacy = CompletableFuture
                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(
                        () -> matchService.getPrivacySettings()));

        var chatPrivacy = CompletableFuture
                .supplyAsync(new SecurityContextAwareAsyncSupplier<>(
                        () -> chatService.getPrivacySettings()));

        CompletableFuture.allOf(matchPrivacy, chatPrivacy).get();

        var mp = matchPrivacy.get();

        var cp = chatPrivacy.get();

        return ServerResponse.ok().body(new PrivacyDto(cp.friends(), mp.matchHistory(),
                mp.matchStats(), mp.playerStats()));
    }

}
