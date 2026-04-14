package net.chess_platform.matchmaking_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class RelayServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.relay-service}")
    private String ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.relay-service-fanout}")
    private String FANOUT_ROUTING_KEY;

    private final RabbitTemplate matchmakingEvents;

    public RelayServiceProxy(@Qualifier("matchmakingEventsRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.matchmakingEvents = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.QUEUE) {
            matchmakingEvents.convertAndSend(FANOUT_ROUTING_KEY, e);
        }
    }

    @Override
    public void publish(AckEvent e) {

    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

}
