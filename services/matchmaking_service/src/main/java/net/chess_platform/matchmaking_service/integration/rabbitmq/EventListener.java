package net.chess_platform.matchmaking_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.matchmaking_service.exception.UserAlreadyExistsException;
import net.chess_platform.matchmaking_service.service.PlayerService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private final DomainEventService eventService;

    private final PlayerService playerService;

    public EventListener(DomainEventService eventService, PlayerService playerService) {
        this.eventService = eventService;
        this.playerService = playerService;
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
