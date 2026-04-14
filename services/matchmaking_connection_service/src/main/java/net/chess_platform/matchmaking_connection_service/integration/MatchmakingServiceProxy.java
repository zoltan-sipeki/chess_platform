package net.chess_platform.matchmaking_connection_service.integration;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply;
import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply.ErrorCause;
import net.chess_platform.common.domain_events.broker.message.queue.backend.MatchmakingReply;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DequeueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.EnqueueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.PlayerDisconnectedMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.StartPrivateMatchMessage;
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

    public String enqueue(CurrentUser user, String matchType) {
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
        } else if (reply instanceof MatchmakingReply r) {
            return r.matchmakingToken();
        }

        return null;
    }

    public void dequeue(CurrentUser user) {
        var dequeueEvent = new DequeueMessage(user.id());
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(dequeueEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
    }

    public String startPrivateMatch(CurrentUser user, UUID inviteeId) {
        var startPrivateMatchEvent = new StartPrivateMatchMessage(user.id(), inviteeId);
        var reply = matchmakingServiceRabbitTemplate.convertSendAndReceive(startPrivateMatchEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }

        if (reply instanceof ErrorReply r) {
            if (r.cause() == ErrorCause.MATCHMAKING_ERROR) {
                throw new MatchmakingException(r.message());
            }
        }
        if (reply instanceof MatchmakingReply r) {
            return r.matchmakingToken();
        }

        return null;

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
