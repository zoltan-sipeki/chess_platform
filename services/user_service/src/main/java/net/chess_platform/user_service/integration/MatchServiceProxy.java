package net.chess_platform.user_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class MatchServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.match-service}")
    private String MATCH_SERVICE_ROUTING_KEY;

    private RabbitTemplate userEvents;

    public MatchServiceProxy(@Qualifier("userEventsRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.userEvents = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.USER) {
            userEvents.convertAndSend(MATCH_SERVICE_ROUTING_KEY, e);
        }

    }

    @Override
    public void publish(AckEvent e) {

    }

    @Override
    public String getName() {
        return MATCH_SERVICE_ROUTING_KEY;
    }

}
