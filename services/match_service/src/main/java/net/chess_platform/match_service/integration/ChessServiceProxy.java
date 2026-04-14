package net.chess_platform.match_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class ChessServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.chess-service}")
    private String NAME;

    private RabbitTemplate chessEvents;

    public ChessServiceProxy(@Qualifier("chessEventsRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.chessEvents = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.CHESS) {
            chessEvents.convertAndSend(e);
        }
    }

    @Override
    public void publish(AckEvent e) {
        chessEvents.convertAndSend(e);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
