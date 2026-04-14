package net.chess_platform.chess_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.chess_service.integration.MatchServiceProxy;
import net.chess_platform.chess_service.integration.RelayServiceProxy;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;

@Configuration
public class DomainEventServiceConfig implements IDomainEventSubscriptionConfigurer {

    private RelayServiceProxy relayService;

    private MatchServiceProxy matchService;

    public DomainEventServiceConfig(RelayServiceProxy relayService,
            MatchServiceProxy matchService) {
        this.matchService = matchService;
        this.relayService = relayService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerSubscription(DomainEvent.Type.MATCH_STARTED, matchService,
                true);
        registry.registerSubscription(DomainEvent.Type.MATCH_STARTED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MATCH_ENDED, matchService, true);
        registry.registerSubscription(DomainEvent.Type.MATCH_ENDED, relayService, false);
    }

}
