package net.chess_platform.chess_service.ws;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PlayerConnections {

    @Value("${ws-connections.unauthenticated.timeout-ms}")
    private long UNAUTHENTICATED_CONNECTION_TIMEOUT_MS;

    private final Map<String, Connection> connections = new HashMap<>();

    private final Map<UUID, Connection> playerToConnection = new HashMap<>();

    private final ObjectMapper objectMapper;

    private final Object lock = new Object();

    private PlayerConnections(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UUID getAuthenticatedUserId(WebSocketSession session) {
        var sessionId = session.getId();

        Connection connection;
        synchronized (lock) {
            connection = connections.get(sessionId);
        }

        return connection == null ? null : connection.getUserId();

    }

    public void add(WebSocketSession session) {
        var sessionId = session.getId();
        var connection = new Connection(new ConcurrentWebSocketSessionDecorator(session, 5000, 5 * 1024));

        synchronized (lock) {
            connections.put(sessionId, connection);
        }
    }

    public void remove(WebSocketSession session) {
        var sessionId = session.getId();
        synchronized (lock) {
            var connection = connections.remove(sessionId);

            if (connection != null && connection.isAuthenticated()) {
                playerToConnection.remove(connection.getUserId());
            }
        }
    }

    public void setAuthenticatedUserId(WebSocketSession session, UUID userId) {
        var sessionId = session.getId();

        synchronized (lock) {
            var connection = connections.get(sessionId);
            if (connection == null) {
                return;
            }

            connection.setUserId(userId);

            playerToConnection.put(userId, connection);
        }
    }

    public void disconnect(UUID userId) {
        try {
            Connection connection;
            synchronized (lock) {
                connection = playerToConnection.get(userId);
            }

            if (connection != null) {
                connection.getSession().close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(String sessionId) {
        try {
            Connection connection;
            synchronized (lock) {
                connection = connections.get(sessionId);
            }

            if (connection != null) {
                connection.getSession().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void sendMessage(UUID userId, T message) {
        try {
            Connection connection;
            synchronized (lock) {
                connection = playerToConnection.get(userId);
            }

            if (connection == null) {
                return;
            }

            var session = connection.getSession();
            if (!session.isOpen()) {
                return;
            }
            var m = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void sendMessage(String sessionId, T message) {
        Connection connection;
        synchronized (lock) {
            connection = connections.get(sessionId);
        }

        if (connection == null) {
            return;
        }
        try {
            var session = connection.getSession();
            if (!session.isOpen()) {
                return;
            }
            var m = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(m));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectUnauthenticatedConnections() {
        var toDisconnect = new ArrayList<Connection>();

        synchronized (lock) {
            for (var entry : connections.entrySet()) {
                var connection = entry.getValue();
                if (!connection.isAuthenticated()
                        && connection.getCreatedAt().plus(UNAUTHENTICATED_CONNECTION_TIMEOUT_MS, ChronoUnit.MILLIS)
                                .isBefore(OffsetDateTime.now())) {
                    toDisconnect.add(connection);
                }
            }
        }

        for (var connection : toDisconnect) {
            disconnect(connection);
        }
    }

    private void disconnect(Connection connection) {
        try {
            connection.getSession().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Connection {

        private final WebSocketSession session;

        private final OffsetDateTime createdAt = OffsetDateTime.now();

        private volatile UUID userId;

        public Connection(WebSocketSession session) {
            this.session = session;
        }

        public WebSocketSession getSession() {
            return session;
        }

        public OffsetDateTime getCreatedAt() {
            return createdAt;
        }

        public boolean isAuthenticated() {
            return userId != null;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

    }
}
