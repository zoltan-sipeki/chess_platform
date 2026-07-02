package net.chess_platform.chess_service.integration;

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
    private String ROUTING_KEY;

    private RabbitTemplate chessEvents;
    
    public MatchServiceProxy(@Qualifier("chessEventsRabbitTemplate") RabbitTemplate chessEventsRabbitTemplate) {
        this.chessEvents = chessEventsRabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        chessEvents.convertAndSend(ROUTING_KEY, e);
    }

    @Override
    public void publish(AckEvent e) {
    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

}
