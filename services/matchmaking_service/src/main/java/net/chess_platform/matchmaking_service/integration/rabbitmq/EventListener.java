package net.chess_platform.matchmaking_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.matchmaking_service.service.UserEventService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private final UserEventService userEventService;

    public EventListener(UserEventService userEventService) {
        this.userEventService = userEventService;
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        userEventService.process(e);
    }
}
