package net.chess_platform.chess_service.integration;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class MatchmakingServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.matchmaking-service}")
    private String ROUTING_KEY;

    private final RestClient restClient;

    private final RabbitTemplate chessEvents;

    private enum MatchStatus {
        ACTIVE
    }

    private static record UpdateMatchRoutingDto(MatchStatus matchStatus) {
    }

    public MatchmakingServiceProxy(@Qualifier("oauth2RestClientBuilder") RestClient.Builder builder,
            @Qualifier("chessEventsRabbitTemplate") RabbitTemplate chessEvents) {
        this.restClient = builder.baseUrl("http://matchmaking-service").build();
        this.chessEvents = chessEvents;
    }

    public void updateMatchRouting(long matchId) {
        restClient.patch().uri("/api/matchmaking/matches/{matchId}", matchId)
                .body(new UpdateMatchRoutingDto(MatchStatus.ACTIVE)).retrieve().toBodilessEntity();
    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        if (e.getCategory() == DomainEvent.Category.CHESS) {
            chessEvents.convertAndSend(ROUTING_KEY, e);
        }
    }

    @Override
    public void publish(AckEvent e) {
    }
}
