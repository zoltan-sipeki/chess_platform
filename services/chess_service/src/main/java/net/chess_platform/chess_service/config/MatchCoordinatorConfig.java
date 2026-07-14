package net.chess_platform.chess_service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.chess_service.coordinator.EventQueue;
import net.chess_platform.chess_service.coordinator.Mapper;
import net.chess_platform.chess_service.coordinator.MatchCoordinatorThread;
import net.chess_platform.chess_service.integration.MatchmakingServiceProxy;
import net.chess_platform.chess_service.ws.PlayerConnections;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Configuration
public class MatchCoordinatorConfig {

    @Bean
    public ConcurrentMap<UUID, Long> routingCache() {
        return new ConcurrentHashMap<>();
    }

    public EventQueue eventQueue() {
        return new EventQueue();
    }

    @Bean
    public List<MatchCoordinatorThread> coordinatorThreads(PlayerConnections connections, Mapper mapper,
            DomainEventService eventService, MatchmakingServiceProxy matchmakingService,
            @Qualifier("routingCache") ConcurrentMap<UUID, Long> routingCache) {
        var threads = new ArrayList<MatchCoordinatorThread>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
            var service = new MatchCoordinatorThread(eventQueue(), connections, matchmakingService, mapper,
                    eventService, routingCache);
            threads.add(service);
            service.start();
        }

        return threads;
    }
}
