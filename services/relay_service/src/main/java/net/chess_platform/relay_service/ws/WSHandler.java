package net.chess_platform.relay_service.ws;

import java.util.UUID;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import net.chess_platform.relay_service.exception.AccessDeniedException;
import net.chess_platform.relay_service.exception.InvalidMessageException;
import net.chess_platform.relay_service.model.RelayUser.Presence;
import net.chess_platform.relay_service.service.RelayUserService;
import net.chess_platform.relay_service.ws.message.ServerMessage;
import net.chess_platform.relay_service.ws.message.client.ClientMessage;
import net.chess_platform.relay_service.ws.message.client.ConnectPayload;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class WSHandler extends TextWebSocketHandler {

    private WSConnections connections;

    private JwtAuthenticationProvider jwtAuthenticationProvider;

    private ObjectMapper objectMapper;

    private RelayUserService relayUserService;

    public WSHandler(WSConnections connections,
            JwtAuthenticationProvider jwtAuthenticationProvider, ObjectMapper objectMapper,
            RelayUserService relayUserService) {
        this.connections = connections;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.objectMapper = objectMapper;
        this.relayUserService = relayUserService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        connections.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connections.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var userId = connections.getAuthenticatedUserId(session);

        try {
            if (userId == null) {
                var id = authenticate(session, message);
                relayUserService.updatePresence(id, Presence.ONLINE);
                connections.setAuthenticatedUserId(session, id);
                connections.sendMessage(id, new ServerMessage(ServerMessage.Type.CONNECTED));
                return;
            }

            var cm = objectMapper.readValue(message.getPayload(), ClientMessage.class);
            var mtype = cm.getType();

            if (mtype == null || mtype == ClientMessage.Type.CONNECT) {
                throw new InvalidMessageException();
            }

        } catch (JacksonException e) {
            connections.sendMessage(session.getId(),
                    new ServerMessage(ServerMessage.Type.ERROR, "Invalid message"));
            connections.disconnect(session.getId());
        } catch (AccessDeniedException e) {
            connections.sendMessage(session.getId(), new ServerMessage(ServerMessage.Type.ERROR, "Unauthorized"));
            connections.disconnect(session.getId());
        }

    }

    private UUID authenticate(WebSocketSession session, TextMessage message) {

        var cm = objectMapper.readValue(message.getPayload(), ClientMessage.class);
        if (cm.getType() != ClientMessage.Type.CONNECT) {
            throw new AccessDeniedException();
        }

        var m = objectMapper.convertValue(cm.getPayload(), ConnectPayload.class);
        return authenticate(session, m);
    }

    private UUID authenticate(WebSocketSession session, ConnectPayload message) {
        var accessToken = message.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            throw new AccessDeniedException();
        }
        try {
            return authenticate(accessToken);
        } catch (AuthenticationException e) {
            throw new AccessDeniedException();
        }
    }

    private UUID authenticate(String accessToken) {
        var authentication = jwtAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(accessToken));
        SecurityContextHolder.clearContext();
        return UUID.fromString(((Jwt) authentication.getPrincipal()).getClaimAsString("sub"));
    }

}
