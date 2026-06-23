package net.chess_platform.matchmaking_service.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.matchmaking_service.integration.ChatServiceProxy;
import net.chess_platform.matchmaking_service.integration.RelayServiceProxy;
import net.chess_platform.matchmaking_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private final RelayServiceProxy relayService;

    private final UserServiceProxy userService;

    private final ChatServiceProxy chatService;

    public DomainEventConfig(RelayServiceProxy relayService, UserServiceProxy userService,
            ChatServiceProxy chatService) {
        this.relayService = relayService;
        this.userService = userService;
        this.chatService = chatService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        var services = List.of(relayService, chatService);
        registry.registerSubscription(DomainEvent.Type.MATCH_FOUND, services, false);
        registry.registerSubscription(DomainEvent.Type.ACTIVITY_CHANGED, services, false);

        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
    }

}
