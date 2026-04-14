package net.chess_platform.matchmaking_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply;
import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply.ErrorCause;
import net.chess_platform.common.domain_events.broker.message.queue.backend.MatchmakingReply;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DequeueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.EnqueueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.PlayerDisconnectedMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.StartPrivateMatchMessage;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.matchmaking_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_service.integration.RelayServiceProxy;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.service.MatchmakingService;

@Component
@RabbitListener(queues = "#{replyQueue.name}", messageConverter = "messageConverter")
public class MMReplyListener {

    private final MatchmakingService matchmakingService;

    private final RelayServiceProxy relayService;

    public MMReplyListener(MatchmakingService matchmakingService,
            RelayServiceProxy relayService) {
        this.matchmakingService = matchmakingService;
        this.relayService = relayService;
    }

    @RabbitHandler
    public Message<?> enqueue(@Payload EnqueueMessage e) {
        try {
            var match = matchmakingService.enqueuePlayer(e.userId(), Match.Type.valueOf(e.matchType().toUpperCase()));
            if (match == null) {
                return MessageBuilder.withPayload(new MatchmakingReply(null)).build();
            }

            var tokens = matchmakingService.createMMTokens(match);

            var otherPlayerId = match.getPlayers().stream().filter(p -> !p.getId().equals(e.userId()))
                    .findFirst()
                    .get().getId();

            relayService
                    .publish(new MatchFoundEvent(otherPlayerId, tokens.get(otherPlayerId)));

            return MessageBuilder.withPayload(new MatchmakingReply(tokens.get(e.userId()))).build();
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
    public Message<?> startPrivateMatch(@Payload StartPrivateMatchMessage message) {
        try {
            var inviterId = message.inviterId();
            var inviteeId = message.inviteeId();
            var tokens = matchmakingService.createMMTokensForPrivateMatch(inviterId, inviteeId);

            relayService
                    .publish(
                            new MatchFoundEvent(inviteeId, tokens.get(inviteeId)));

            return MessageBuilder.withPayload(new MatchmakingReply(tokens.get(inviterId))).build();
        } catch (MatchmakingException e) {
            return MessageBuilder.withPayload(new ErrorReply(ErrorCause.MATCHMAKING_ERROR, e.getMessage())).build();
        }
    }
}
