package net.chess_platform.gateway.handler;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.netflix.discovery.EurekaClient;

@Component
public class ChessWebSocketProxy extends AbstractWebSocketProxy {

    private final EurekaClient discoveryClient;

    public ChessWebSocketProxy(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public String computeTargetURI(URI sourceURI) {
        var query = sourceURI.getQuery();
        var params = query.split("&");
        for (var param : params) {
            var entry = param.split("=");
            if (!entry[0].equals("target")) {
                continue;
            }

            var instances = discoveryClient.getInstancesByVipAddress("chess-service", false);
            for (var instance : instances) {
                var uuid = instance.getMetadata().get("uuid");
                if (uuid != null && uuid.equals(entry[1])) {
                    return "ws://" + instance.getHostName() + ":" + instance.getPort() + "/ws";
                }
            }
        }

        throw new IllegalArgumentException("Invalid target");
    }

}
