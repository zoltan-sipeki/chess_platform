package net.chess_platform.gateway.handler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public abstract class AbstractWebSocketProxy extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> clientToProxy = new HashMap<>();

    private final Map<String, WebSocketSession> proxyToClient = new HashMap<>();

    private final Map<String, List<WebSocketMessage<?>>> clientBuffers = new HashMap<>();

    private final Object lock = new Object();

    public abstract String computeTargetURI(URI sourceURI);

    @Override
    public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {
        var client = new StandardWebSocketClient();

        var future = client.execute(this.new ProxiedWebSocketHandler(),
                computeTargetURI(clientSession.getUri()));

        future.whenComplete((proxiedSession, exception) -> {
            if (exception != null) {
                try {
                    clientSession.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }

            List<WebSocketMessage<?>> buffer;
            synchronized (lock) {
                var c = new ConcurrentWebSocketSessionDecorator(clientSession, 10, 5 * 1024);
                var p = new ConcurrentWebSocketSessionDecorator(proxiedSession, 10, 5 * 1024);

                clientToProxy.put(c.getId(), p);
                proxyToClient.put(p.getId(), c);
                buffer = clientBuffers.remove(clientSession.getId());
            }

            if (buffer == null) {
                return;
            }

            try {
                for (var bufferMessage : buffer) {
                    proxiedSession.sendMessage(bufferMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    @Override
    public void afterConnectionClosed(WebSocketSession clientSession, CloseStatus status) throws Exception {
        WebSocketSession proxiedSession;

        synchronized (lock) {
            proxiedSession = clientToProxy.remove(clientSession.getId());
            if (proxiedSession != null) {
                proxyToClient.remove(proxiedSession.getId());
            }

            clientBuffers.remove(clientSession.getId());
        }

        if (proxiedSession != null) {
            proxiedSession.close();
        }

    }

    @Override
    public void handleMessage(WebSocketSession clientSession, WebSocketMessage<?> message) throws Exception {
        WebSocketSession proxiedSession;

        synchronized (lock) {
            proxiedSession = clientToProxy.get(clientSession.getId());

            if (proxiedSession == null) {
                clientBuffers.computeIfAbsent(clientSession.getId(), k -> new ArrayList<>())
                        .add(message);
            }
        }

        if (proxiedSession != null) {
            proxiedSession.sendMessage(message);
        }

    }

    private class ProxiedWebSocketHandler extends TextWebSocketHandler {

        @Override
        public void handleMessage(WebSocketSession proxiedSession, WebSocketMessage<?> message) throws Exception {
            WebSocketSession clientSession;

            synchronized (lock) {
                clientSession = proxyToClient.get(proxiedSession.getId());
            }

            if (clientSession != null) {
                clientSession.sendMessage(message);
            }

        }

        @Override
        public void afterConnectionClosed(WebSocketSession proxiedSession, CloseStatus status) throws Exception {
            WebSocketSession clientSession;

            synchronized (lock) {
                clientSession = proxyToClient.get(proxiedSession.getId());
            }

            if (clientSession != null) {
                clientSession.close();
            }
        }
    }
}
