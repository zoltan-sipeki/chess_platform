package net.chess_platform.matchmaking_service.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventConfigurer;
import net.chess_platform.matchmaking_service.integration.ChatServiceProxy;
import net.chess_platform.matchmaking_service.integration.ChessServiceProxy;
import net.chess_platform.matchmaking_service.integration.RelayServiceProxy;
import net.chess_platform.matchmaking_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventConfigurer {

    @Value("${rabbitmq-messaging.routing-key.service}")
    public String SERVICE_NAME;

    private final RelayServiceProxy relayService;

    private final UserServiceProxy userService;

    private final ChatServiceProxy chatService;

    private final ChessServiceProxy chessService;

    public DomainEventConfig(RelayServiceProxy relayService, UserServiceProxy userService,
            ChatServiceProxy chatService, ChessServiceProxy chessService) {
        this.relayService = relayService;
        this.userService = userService;
        this.chatService = chatService;
        this.chessService = chessService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerSubscription(DomainEvent.Type.MATCH_FOUND, relayService, false);
        registry.registerSubscription(DomainEvent.Type.ACTIVITY_CHANGED, List.of(relayService, chatService), false);

        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
        registry.registerAck(DomainEvent.Type.MATCH_ENDED, chessService);
    }

    @Override
    public void configure(DomainEventService service) {
        service.setServiceName(SERVICE_NAME);
    }

}
