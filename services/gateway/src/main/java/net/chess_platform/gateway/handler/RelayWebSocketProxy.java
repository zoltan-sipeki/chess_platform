package net.chess_platform.gateway.handler;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.netflix.discovery.EurekaClient;

@Component
public class RelayWebSocketProxy extends AbstractWebSocketProxy {

    private final EurekaClient discoveryClient;

    public RelayWebSocketProxy(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public String computeTargetURI(URI sourceURI) {
        var instance = discoveryClient.getNextServerFromEureka("relay_service", false);
        return "ws://" + instance.getHostName() + ":" + instance.getPort() + "/ws";
    }

}
