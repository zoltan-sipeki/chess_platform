package net.chess_platform.chess_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private DomainEventService eventService;

    public EventListener(DomainEventService eventService) {
        this.eventService = eventService;
    }

    @RabbitHandler
    public void confirmAck(@Payload AckEvent message) {
        eventService.confirmAck(message);
    }
}
