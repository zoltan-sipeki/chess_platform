package net.chess_platform.chat_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.chat_service.integration.RelayServiceProxy;
import net.chess_platform.chat_service.integration.UserServiceProxy;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private RelayServiceProxy relayService;

    private UserServiceProxy userService;

    public DomainEventConfig(RelayServiceProxy relayService, UserServiceProxy userService) {
        this.relayService = relayService;
        this.userService = userService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerSubscription(DomainEvent.Type.MESSAGE_CREATED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MESSAGE_EDITED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.MESSAGE_DELETED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.GROUP_CHANNEL_CREATED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.GROUP_CHANNEL_MEMBER_JOINED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.GROUP_CHANNEL_MEMBER_LEFT, relayService, false);
        registry.registerSubscription(DomainEvent.Type.GROUP_CHANNEL_ROLE_CHANGED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.NOTIFICATION, relayService, false);
        registry.registerSubscription(DomainEvent.Type.UNFRIEND, relayService, false);

        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
    }

}
