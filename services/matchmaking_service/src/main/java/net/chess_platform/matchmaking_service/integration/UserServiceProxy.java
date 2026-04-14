package net.chess_platform.matchmaking_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class UserServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.user-service}")
    private String USER_SERVICE_ROUTING_KEY;

    private final RabbitTemplate userEvents;

    public UserServiceProxy(@Qualifier("userEventsRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.userEvents = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
    }

    @Override
    public void publish(AckEvent e) {
        if (e.getAckedCategory() == DomainEvent.Category.USER) {
            userEvents.convertAndSend(USER_SERVICE_ROUTING_KEY, e);
        }
    }

    @Override
    public String getName() {
        return USER_SERVICE_ROUTING_KEY;
    }

}
