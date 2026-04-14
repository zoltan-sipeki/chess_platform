package net.chess_platform.user_service.integration.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.service.UserService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private final DomainEventService domainEventService;

    private final UserService userService;

    public EventListener(DomainEventService domainEventService, UserService userService) {
        this.domainEventService = domainEventService;
        this.userService = userService;
    }

    @RabbitHandler
    public void process(@Payload AckEvent e, Message message) {
        domainEventService.confirmAck(e);
    }

    @RabbitHandler
    public void process(@Payload KeycloakUserVerifiedMessage m) {
        userService.process(m);
    }
}
