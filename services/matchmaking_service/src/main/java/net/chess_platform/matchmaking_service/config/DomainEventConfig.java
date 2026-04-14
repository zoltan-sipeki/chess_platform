package net.chess_platform.matchmaking_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.matchmaking_service.integration.RelayServiceProxy;
import net.chess_platform.matchmaking_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private final RelayServiceProxy relayService;

    private final UserServiceProxy userService;

    public DomainEventConfig(RelayServiceProxy relayService, UserServiceProxy userService) {
        this.relayService = relayService;
        this.userService = userService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerSubscription(DomainEvent.Type.MATCH_FOUND, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MATCH_FOUND_BROADCAST, relayService, false);

        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
    }

}
