package net.chess_platform.chat_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.chat_service.service.UserService;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private UserService userService;

    public EventListener(UserService userService) {
        this.userService = userService;
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        userService.process(e);
    }

    @RabbitHandler
    public void process(@Payload UserUpdatedEvent e) {
        userService.process(e);
    }

}
