package net.chess_platform.match_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.match_service.exception.MatchAlreadyExistsException;
import net.chess_platform.match_service.exception.UserAlreadyExistsException;
import net.chess_platform.match_service.service.MatchService;
import net.chess_platform.match_service.service.PlayerService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private final DomainEventService eventService;

    private final PlayerService playerService;

    private final MatchService matchService;

    public EventListener(DomainEventService eventService,
            PlayerService playerService, MatchService matchService) {
        this.eventService = eventService;
        this.playerService = playerService;
        this.matchService = matchService;
    }

    @RabbitHandler
    public void process(@Payload MatchEndedEvent e) {
        try {
            matchService.process(e);
            eventService.ack(e, SERVICE_NAME);
        } catch (MatchAlreadyExistsException ex) {
            eventService.ack(e, SERVICE_NAME);
        }
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        try {
            playerService.process(e);
            eventService.ack(e, SERVICE_NAME);
        } catch (UserAlreadyExistsException ex) {
            eventService.ack(e, SERVICE_NAME);
        }
    }
}
