package net.chess_platform.relay_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.relay_service.integration.ChatServiceProxy;
import net.chess_platform.relay_service.integration.MatchmakingServiceProxy;
import net.chess_platform.relay_service.integration.RelayServiceProxy;
import net.chess_platform.relay_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private final UserServiceProxy userService;

    private final ChatServiceProxy chatService;

    private final RelayServiceProxy relayService;

    private final MatchmakingServiceProxy matchmakingService;

    public DomainEventConfig(UserServiceProxy userService, RelayServiceProxy relayService,
            ChatServiceProxy chatService, MatchmakingServiceProxy matchmakingService) {
        this.userService = userService;
        this.relayService = relayService;
        this.chatService = chatService;
        this.matchmakingService = matchmakingService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
        registry.registerSubscription(DomainEvent.Type.PRESENCE_CHANGED, relayService, false);
        registry.registerSubscription(DomainEvent.Type.PRESENCE_CHANGED, chatService, false);
        registry.registerSubscription(DomainEvent.Type.RELAY_DISCONNECT, matchmakingService, false);
    }

}
