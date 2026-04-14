package net.chess_platform.relay_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.relay_service.integration.RelayServiceProxy;
import net.chess_platform.relay_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private UserServiceProxy userService;

    private RelayServiceProxy relayService;

    public DomainEventConfig(UserServiceProxy userService, RelayServiceProxy relayService) {
        this.userService = userService;
        this.relayService = relayService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
        registry.registerSubscription(DomainEvent.Type.PRESENCE_CHANGED, relayService, false);
    }

}
