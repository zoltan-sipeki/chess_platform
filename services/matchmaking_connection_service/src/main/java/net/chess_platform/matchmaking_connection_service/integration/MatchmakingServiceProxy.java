package net.chess_platform.matchmaking_connection_service.integration;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply;
import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply.ErrorCause;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.CreatePrivateMatchMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DequeueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.EnqueueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.PlayerDisconnectedMessage;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_connection_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_connection_service.exception.ServiceUnavailableException;

@Service
public class MatchmakingServiceProxy {

    private RabbitTemplate matchmakingServiceRabbitTemplate;

    public MatchmakingServiceProxy(
            @Qualifier("matchmakingServiceRabbitTemplate") RabbitTemplate matchmakingServiceRabbitTemplate) {
        this.matchmakingServiceRabbitTemplate = matchmakingServiceRabbitTemplate;
    }

    public void enqueue(CurrentUser user, String matchType) {
        var enqueueEvent = new EnqueueMessage(user.id(), matchType);
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(enqueueEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }

        if (reply instanceof ErrorReply r) {
            if (r.cause() == ErrorCause.MATCHMAKING_ERROR) {
                throw new MatchmakingException(r.message());
            }

            if (r.cause() == ErrorCause.SERVICE_UNAVAILABLE) {
                throw new ServiceUnavailableException(r.message());
            }
        }
    }

    public void dequeue(CurrentUser user) {
        var dequeueEvent = new DequeueMessage(user.id());
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(dequeueEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
    }

    public void createPrivateMatch(CurrentUser user, UUID inviteeId) {
        var m = new CreatePrivateMatchMessage(user.id(), inviteeId);
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(m);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }

        if (reply instanceof ErrorReply r) {
            if (r.cause() == ErrorCause.MATCHMAKING_ERROR) {
                throw new MatchmakingException(r.message());
            }
        }
    }

    public void disconnect(UUID userId) {
        var disconnectEvent = new PlayerDisconnectedMessage(userId);
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(disconnectEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
    }
}
