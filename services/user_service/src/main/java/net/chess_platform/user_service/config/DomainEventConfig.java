package net.chess_platform.user_service.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.user_service.integration.ChatServiceProxy;
import net.chess_platform.user_service.integration.MatchServiceProxy;
import net.chess_platform.user_service.integration.MatchmakingServiceProxy;
import net.chess_platform.user_service.integration.RelayServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

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
        registry.registerSubscription(DomainEvent.Type.USER_UPDATED, services, true);
        registry.registerSubscription(DomainEvent.Type.USER_DELETED, services, true);
    }

}
