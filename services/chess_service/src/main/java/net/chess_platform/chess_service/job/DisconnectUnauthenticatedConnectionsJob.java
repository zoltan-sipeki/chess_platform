package net.chess_platform.chess_service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.chess_service.ws.PlayerConnections;

@Component
public class DisconnectUnauthenticatedConnectionsJob {

    private final PlayerConnections connections;

    public DisconnectUnauthenticatedConnectionsJob(PlayerConnections connections) {
        this.connections = connections;
    }

    @Scheduled(fixedDelayString = "${ws-connections.unauthenticated.timeout-ms}", initialDelayString = "${ws-connections.unauthenticated.timeout-ms}")
    public void run() {
        connections.disconnectUnauthenticatedConnections();
    }
}
