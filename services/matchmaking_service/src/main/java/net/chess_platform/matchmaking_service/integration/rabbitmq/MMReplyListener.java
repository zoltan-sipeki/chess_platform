package net.chess_platform.matchmaking_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply;
import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply.ErrorCause;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.CreatePrivateMatchMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DequeueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.EnqueueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.PlayerDisconnectedMessage;
import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.service.MatchmakingService;

@Component
@RabbitListener(queues = "#{replyQueue.name}", messageConverter = "messageConverter")
public class MMReplyListener {

    private final MatchmakingService matchmakingService;

    public MMReplyListener(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @RabbitHandler
    public Message<?> enqueue(@Payload EnqueueMessage e) {
        try {
            matchmakingService.enqueuePlayer(e.userId(), Match.Type.valueOf(e.matchType().toUpperCase()));

            return MessageBuilder.withPayload(true).build();
        } catch (MatchmakingException ex) {
            return MessageBuilder.withPayload(new ErrorReply(ErrorCause.MATCHMAKING_ERROR, ex.getMessage())).build();
        } catch (ServiceUnavailableException ex) {
            return MessageBuilder.withPayload(new ErrorReply(ErrorCause.SERVICE_UNAVAILABLE, ex.getMessage())).build();
        }
    }

    @RabbitHandler
    public boolean dequeue(@Payload DequeueMessage e) {
        return matchmakingService.dequeuePlayer(e.userId());
    }

    @RabbitHandler
    public void disconnect(@Payload PlayerDisconnectedMessage e) {
        matchmakingService.dequeuePlayer(e.userId());
    }

    @RabbitHandler
    public Message<?> startPrivateMatch(@Payload CreatePrivateMatchMessage message) {
        try {
            matchmakingService.startPrivateMatch(message.inviterId(), message.inviteeId());

            return MessageBuilder.withPayload(true).build();
        } catch (MatchmakingException e) {
            return MessageBuilder.withPayload(new ErrorReply(ErrorCause.MATCHMAKING_ERROR, e.getMessage())).build();
        } catch (ServiceUnavailableException e) {
            return MessageBuilder.withPayload(new ErrorReply(ErrorCause.SERVICE_UNAVAILABLE, e.getMessage())).build();
        }
    }
}
