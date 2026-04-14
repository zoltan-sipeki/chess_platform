package net.chess_platform.chess_service.ws;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
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

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.chess_platform.chess_service.coordinator.MatchCoordinator;
import net.chess_platform.chess_service.coordinator.MMTokenParser;
import net.chess_platform.chess_service.exception.AccessDeniedException;
import net.chess_platform.chess_service.exception.InvalidMessageException;
import net.chess_platform.chess_service.ws.message.client.ClientMessage;
import net.chess_platform.chess_service.ws.message.client.ClientMessage.Type;
import net.chess_platform.chess_service.ws.message.client.ConnectPayload;
import net.chess_platform.chess_service.ws.message.client.DisconnectPayload;
import net.chess_platform.chess_service.ws.message.client.IChessMessage;
import net.chess_platform.chess_service.ws.message.client.MMTokenPayload;
import net.chess_platform.chess_service.ws.message.server.ServerMessage;

@Component
public class WSHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    private final List<MatchCoordinator> coordinatorThreads;

    private final MMTokenParser mmTokenParser;

    private final PlayerConnections connections;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public WSHandler(ObjectMapper objectMapper,
            @Qualifier("coordinatorThreads") List<MatchCoordinator> coordinatorThreads,
            MMTokenParser mmTokenParser,
            PlayerConnections connections,
            JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.objectMapper = objectMapper;
        this.coordinatorThreads = coordinatorThreads;
        this.mmTokenParser = mmTokenParser;
        this.connections = connections;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        connections.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        var userId = connections.getAuthenticatedUserId(session);
        long matchId = getMatchIdByUserId(userId);
        if (matchId > 0) {
            dispatchMessage(new DisconnectPayload(userId, matchId));
        }

        connections.remove(session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var userId = connections.getAuthenticatedUserId(session);

        try {
            if (userId == null) {
                authenticate(session, message);
                return;
            }

            var cm = objectMapper.readValue(message.getPayload(), ClientMessage.class);
            var mtype = cm.getType();

            if (mtype == null || mtype == Type.CONNECT) {
                throw new InvalidMessageException();
            }

            var m = objectMapper.convertValue(cm.getPayload(), ClientMessage.PAYLOAD_MAPPING.get(mtype));
            checkNotEmpty(m);

            if (mtype == Type.MATCHMAKING_TOKEN) {
                handleMMTokenMessage((MMTokenPayload) m, userId);
            } else {
                var chessMessage = (IChessMessage) m;
                chessMessage.setUserId(userId);
                dispatchMessage(chessMessage);
            }

        } catch (JsonProcessingException | InvalidMessageException e) {
            connections.sendMessage(session.getId(),
                    new ServerMessage(ServerMessage.Type.ERROR, "Invalid message"));
            connections.disconnect(session.getId());
        } catch (AccessDeniedException e) {
            connections.sendMessage(session.getId(), new ServerMessage(ServerMessage.Type.ERROR, "Unauthorized"));
            connections.disconnect(session.getId());
        } catch (JWTVerificationException e) {
            connections.sendMessage(session.getId(),
                    new ServerMessage(ServerMessage.Type.ERROR, "Invalid matchmaking token"));
            connections.disconnect(session.getId());
        }

    }

    private void handleMMTokenMessage(MMTokenPayload message, UUID userId) throws InvalidMessageException {
        if (getMatchIdByUserId(userId) > 0) {
            connections.sendMessage(userId, new ServerMessage(ServerMessage.Type.ERROR, "Already in match"));
            connections.disconnect(userId);
            return;
        }

        var token = mmTokenParser.verifyMatchmakingToken(message.getToken());
        if (!token.getUserId().equals(userId)) {
            connections.sendMessage(userId, new ServerMessage(ServerMessage.Type.ERROR, "Invalid token"));
            connections.disconnect(userId);
        } else {
            dispatchMessage(token);
        }
    }

    private long getMatchIdByUserId(UUID userId) {
        if (userId == null) {
            return 0;
        }

        for (var coordinator : coordinatorThreads) {
            long id = coordinator.getMatchIdByUserId(userId);
            if (id > 0) {
                return id;
            }
        }
        return 0;
    }

    private void checkNotEmpty(Object object) {
        var fields = object.getClass().getDeclaredFields();
        if (fields.length == 0) {
            throw new InvalidMessageException();
        }

        for (var field : fields) {
            field.setAccessible(true);
            try {
                var f = field.get(object);
                if (field.get(object) == null || f instanceof String && ((String) f).isEmpty()) {
                    field.setAccessible(false);
                    throw new InvalidMessageException();
                }
                field.setAccessible(false);
            } catch (IllegalArgumentException | IllegalAccessException e) {
            }

        }
    }

    private void authenticate(WebSocketSession session, TextMessage message)
            throws JsonProcessingException, AccessDeniedException {
        var cm = objectMapper.readValue(message.getPayload(), ClientMessage.class);
        if (cm.getType() != ClientMessage.Type.CONNECT) {
            throw new AccessDeniedException();
        }

        var m = objectMapper.convertValue(cm.getPayload(), ConnectPayload.class);
        authenticate(session, m);
    }

    private void authenticate(WebSocketSession session, ConnectPayload message) {
        var accessToken = message.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            throw new AccessDeniedException();
        }
        try {
            var currentUserId = authenticate(accessToken);
            connections.setAuthenticatedUserId(session, currentUserId);
            connections.sendMessage(currentUserId, new ServerMessage(ServerMessage.Type.CONNECTED));
        } catch (AuthenticationException e) {
            throw new AccessDeniedException();
        }
    }

    private UUID authenticate(String accessToken) {
        var authentication = jwtAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(accessToken));
        SecurityContextHolder.clearContext();
        return UUID.fromString(((Jwt) authentication.getPrincipal()).getClaimAsString("sub"));
    }

    private void dispatchMessage(IChessMessage message) {
        coordinatorThreads.get(((int) (message.getMatchId() % coordinatorThreads.size())))
                .enqueueMessage(message);
    }
}
