package net.chess_platform.match_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.match_service.service.MatchService;
import net.chess_platform.match_service.service.PlayerService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private final PlayerService playerService;

    private final MatchService matchService;

    public EventListener(PlayerService playerService, MatchService matchService) {
        this.playerService = playerService;
        this.matchService = matchService;
    }

    @RabbitHandler
    public void process(@Payload MatchEndedEvent e) {
        matchService.process(e);
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        playerService.process(e);
    }

    @RabbitHandler
    public void process(@Payload UserUpdatedEvent e) {
        playerService.process(e);
    }
}
