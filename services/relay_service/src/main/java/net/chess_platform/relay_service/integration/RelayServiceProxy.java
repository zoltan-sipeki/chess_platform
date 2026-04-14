package net.chess_platform.relay_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class RelayServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.service-fanout}")
    private String FANOUT_ROUTING_KEY;

    private RabbitTemplate relayEvents;

    public RelayServiceProxy(@Qualifier("relayEventsRabbitTemplate") RabbitTemplate relayEvents) {
        this.relayEvents = relayEvents;
    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.SOCIAL) {
            relayEvents.convertAndSend(FANOUT_ROUTING_KEY, e);
        }
    }

    @Override
    public void publish(AckEvent e) {
    }
}
