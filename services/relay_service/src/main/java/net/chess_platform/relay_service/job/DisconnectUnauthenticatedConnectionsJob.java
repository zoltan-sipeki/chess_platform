package net.chess_platform.relay_service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.relay_service.ws.WSConnections;

@Component
public class DisconnectUnauthenticatedConnectionsJob {

    private final WSConnections connections;

    public DisconnectUnauthenticatedConnectionsJob(WSConnections connections) {
        this.connections = connections;
    }

    @Scheduled(fixedDelayString = "${ws-connections.unauthenticated.timeout-ms}", initialDelayString = "${ws-connections.unauthenticated.timeout-ms}")
    public void run() {
        connections.disconnectUnauthenticatedConnections();
    }
}
