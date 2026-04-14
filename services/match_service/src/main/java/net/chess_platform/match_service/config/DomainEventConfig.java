package net.chess_platform.match_service.config;

import org.springframework.context.annotation.Configuration;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;
import net.chess_platform.common.domain_events.service.IDomainEventSubscriptionConfigurer;
import net.chess_platform.match_service.integration.ChessServiceProxy;
import net.chess_platform.match_service.integration.UserServiceProxy;

@Configuration
public class DomainEventConfig implements IDomainEventSubscriptionConfigurer {

    private ChessServiceProxy chessService;

    private UserServiceProxy userService;

    public DomainEventConfig(ChessServiceProxy chessService, UserServiceProxy userService) {
        this.chessService = chessService;
        this.userService = userService;
    }

    @Override
    public void configure(DomainEventSubscriptionRegistry registry) {
        registry.registerAck(DomainEvent.Type.MATCH_ENDED, chessService);
        registry.registerAck(DomainEvent.Type.USER_CREATED, userService);
    }

}
