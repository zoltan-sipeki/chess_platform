package net.chess_platform.relay_service.integration;

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
    private String ROUTING_KEY;

    private RabbitTemplate userEvents;

    public UserServiceProxy(@Qualifier("userEventsRabbitTemplate") RabbitTemplate userEvents) {
        this.userEvents = userEvents;
    }

    @Override
    public void publish(DomainEvent<?> e) {
    }

    @Override
    public void publish(AckEvent e) {
        if (e.getAckedCategory() == DomainEvent.Category.USER) {
            userEvents.convertAndSend(ROUTING_KEY, e);
        }
    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

}
