package net.chess_platform.user_service.integration;

import net.chess_platform.user_service.controller.AvatarController;
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
    private String RELAY_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.relay-service-fanout}")
    private String RELAY_SERVICE_FANOUT_ROUTING_KEY;

    private RabbitTemplate userEvents;

    public RelayServiceProxy(@Qualifier("userEventsRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.userEvents = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.USER) {
            if (e.getType() == DomainEvent.Type.USER_CREATED) {
                userEvents.convertAndSend(RELAY_SERVICE_ROUTING_KEY, e);
            } else {
                userEvents.convertAndSend(RELAY_SERVICE_FANOUT_ROUTING_KEY, e);
            }
        }
    }

    @Override
    public void publish(AckEvent e) {

    }

    @Override
    public String getName() {
        return RELAY_SERVICE_ROUTING_KEY;
    }
}
