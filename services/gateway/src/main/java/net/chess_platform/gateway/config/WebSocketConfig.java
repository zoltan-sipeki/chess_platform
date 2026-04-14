package net.chess_platform.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import net.chess_platform.gateway.handler.ChessWebSocketProxy;
import net.chess_platform.gateway.handler.RelayWebSocketProxy;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RelayWebSocketProxy relayProxy;

    private final ChessWebSocketProxy chessProxy;

    public WebSocketConfig(RelayWebSocketProxy relayProxy, ChessWebSocketProxy chessProxy) {
        this.relayProxy = relayProxy;
        this.chessProxy = chessProxy;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(relayProxy, "/relay/ws").setAllowedOrigins("*");
        registry.addHandler(chessProxy, "/chess/ws").setAllowedOrigins("*");
    }

}
