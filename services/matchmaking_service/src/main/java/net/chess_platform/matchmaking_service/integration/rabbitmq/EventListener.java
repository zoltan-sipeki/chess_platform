package net.chess_platform.matchmaking_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.relay.RelayDisconnectEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.matchmaking_service.service.MatchmakingService;
import net.chess_platform.matchmaking_service.service.PlayerService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private final PlayerService playerService;

    private final MatchmakingService matchmakingService;

    public EventListener(PlayerService playerService, MatchmakingService matchmakingService) {
        this.playerService = playerService;
        this.matchmakingService = matchmakingService;
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        playerService.process(e);
    }

    @RabbitHandler
    public void process(@Payload RelayDisconnectEvent e) {
        matchmakingService.process(e);
    }

    @RabbitHandler
    public void process(@Payload MatchEndedEvent e) {
        matchmakingService.process(e);
    }
}
