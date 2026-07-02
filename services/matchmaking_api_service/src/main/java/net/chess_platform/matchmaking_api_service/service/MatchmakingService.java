package net.chess_platform.matchmaking_api_service.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply;
import net.chess_platform.common.domain_events.broker.message.queue.backend.ErrorReply.ErrorCause;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.CreatePrivateMatchMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DeclineMatchMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.DequeueMessage;
import net.chess_platform.common.domain_events.broker.message.queue.frontend.EnqueueMessage;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.matchmaking_api_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_api_service.exception.EntityNotFoundException;
import net.chess_platform.matchmaking_api_service.exception.MatchmakingException;
import net.chess_platform.matchmaking_api_service.exception.ServiceUnavailableException;
import net.chess_platform.matchmaking_api_service.mapper.MatchRoutingMapper;
import net.chess_platform.matchmaking_api_service.model.MatchRouting;
import net.chess_platform.matchmaking_api_service.repository.MatchRoutingRepository;

@Service
public class MatchmakingService {

    private final RabbitTemplate service;

    private final MatchRoutingRepository mmRoutingRepository;

    private final MatchRoutingMapper mapper;

    public MatchmakingService(
            @Qualifier("matchmakingServiceRabbitTemplate") RabbitTemplate matchmakingServiceRabbitTemplate,
            MatchRoutingRepository mmRoutingRepository, MatchRoutingMapper mapper) {
        this.service = matchmakingServiceRabbitTemplate;
        this.mmRoutingRepository = mmRoutingRepository;
        this.mapper = mapper;
    }

    public CurrentMatchDto findCurrentMatch(CurrentUser user) {
        var routingData = mmRoutingRepository.findByPlayerId(user.id())
                .orElseThrow(() -> new EntityNotFoundException());
        if (routingData.getMatchStatus() == MatchRouting.Status.PENDING) {
            return mapper.toDto(routingData);
        }

        return mapper.toDtoWithoutToken(routingData);
    }

    public void enqueue(CurrentUser user, String matchType) {
        var enqueueEvent = new EnqueueMessage(user.id(), matchType);
        var reply = service.convertSendAndReceive(enqueueEvent);

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
        var reply = service.convertSendAndReceive(dequeueEvent);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
    }

    public void createPrivateMatch(CurrentUser user, UUID inviteeId) {
        var m = new CreatePrivateMatchMessage(user.id(), inviteeId);
        var reply = service.convertSendAndReceive(m);

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

    public void deletePendingMatch(CurrentUser user) {
        var m = new DeclineMatchMessage(user.id());
        var reply = service.convertSendAndReceive(m);

        if (reply == null) {
            throw new ServiceUnavailableException(
                    "Matchmaking service is currently unavailable. Please try again later.");
        }
    }
}
