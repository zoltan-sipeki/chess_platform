package net.chess_platform.match_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.match_service.service.ChessEventService;
import net.chess_platform.match_service.service.UserEventService;

@Component
@RabbitListener(queues = "#{eventQueue.name}", messageConverter = "messageConverter")
public class EventListener {

    private ChessEventService chessEventService;

    private UserEventService userEventService;

    public EventListener(ChessEventService chessEventService, UserEventService userEventService) {
        this.chessEventService = chessEventService;
        this.userEventService = userEventService;
    }

    @RabbitHandler
    public void process(@Payload MatchEndedEvent e) {
        chessEventService.process(e);
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        userEventService.process(e);
    }
}
