package net.chess_platform.user_service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventConfigurer;
import net.chess_platform.user_service.integration.ChatServiceProxy;
import net.chess_platform.user_service.integration.MatchServiceProxy;
import net.chess_platform.user_service.integration.MatchmakingServiceProxy;
import net.chess_platform.user_service.integration.RelayServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventConfigurer {

    @Value("${rabbitmq-messaging.routing-key.service}")
    public String SERVICE_NAME;

    private final ChatServiceProxy chatService;

    private final MatchmakingServiceProxy matchmakingService;

    private final MatchServiceProxy matchService;

    private final RelayServiceProxy relayService;

    public DomainEventConfig(ChatServiceProxy chatService, MatchmakingServiceProxy matchmakingService,
            MatchServiceProxy matchService,
            RelayServiceProxy relayService) {
        this.chatService = chatService;
        this.matchmakingService = matchmakingService;
        this.matchService = matchService;
        this.relayService = relayService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        var services = List.of(chatService, matchmakingService, matchService, relayService);
        registry.registerSubscription(DomainEvent.Type.USER_CREATED, services, true);
        registry.registerSubscription(DomainEvent.Type.USER_UPDATED, List.of(chatService, matchService), true);
        registry.registerSubscription(DomainEvent.Type.USER_UPDATED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.USER_DELETED, services, true);
    }

    @Override
    public void configure(DomainEventService service) {
        service.setServiceName(SERVICE_NAME);
    }
}
