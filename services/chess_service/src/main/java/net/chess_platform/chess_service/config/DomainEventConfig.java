package net.chess_platform.chess_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.chess_service.integration.MatchServiceProxy;
import net.chess_platform.chess_service.integration.MatchmakingServiceProxy;
import net.chess_platform.chess_service.integration.RelayServiceProxy;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventConfigurer;

@Configuration
public class DomainEventConfig implements IDomainEventConfigurer {

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String SERVICE_NAME;

    private final RelayServiceProxy relayService;

    private final MatchServiceProxy matchService;

    private final MatchmakingServiceProxy matchmakingService;

    public DomainEventConfig(RelayServiceProxy relayService,
            MatchServiceProxy matchService, MatchmakingServiceProxy matchmakingService) {
        this.matchService = matchService;
        this.relayService = relayService;
        this.matchmakingService = matchmakingService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerSubscription(DomainEvent.Type.MATCH_STARTED, matchService,
                true);
        registry.registerSubscription(DomainEvent.Type.MATCH_STARTED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MATCH_ENDED, matchService, true);
        registry.registerSubscription(DomainEvent.Type.MATCH_ENDED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MATCH_ENDED, matchmakingService, true);
    }

    @Override
    public void configure(DomainEventService service) {
        service.setServiceName(SERVICE_NAME);
    }
}
