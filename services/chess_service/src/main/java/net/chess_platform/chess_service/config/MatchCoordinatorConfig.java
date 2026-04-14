package net.chess_platform.chess_service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.chess_service.coordinator.Mapper;
import net.chess_platform.chess_service.coordinator.MatchCoordinator;
import net.chess_platform.chess_service.integration.MatchServiceProxy;
import net.chess_platform.chess_service.ws.PlayerConnections;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Configuration
public class MatchCoordinatorConfig {

    public ScheduledExecutorService scheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public List<MatchCoordinator> coordinatorThreads(PlayerConnections connections, Mapper mapper,
            DomainEventService eventService,
            MatchServiceProxy matchService) {
        var threads = new ArrayList<MatchCoordinator>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); ++i) {
            var service = new MatchCoordinator(connections, scheduler(), matchService, mapper,
                    eventService);
            threads.add(service);
            service.start();
        }

        return threads;
    }
}
