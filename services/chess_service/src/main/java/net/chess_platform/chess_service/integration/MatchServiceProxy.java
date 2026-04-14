package net.chess_platform.chess_service.integration;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class MatchServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.match-service}")
    private String ROUTING_KEY;

    private RestClient restClient;

    private RabbitTemplate chessEvents;

    public static record OngoingMatchRequest(long matchId, UUID userId, String instance) {
    }

    public MatchServiceProxy(@Qualifier("chessEventsRabbitTemplate") RabbitTemplate chessEventsRabbitTemplate,
            @Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.chessEvents = chessEventsRabbitTemplate;
        restClient = builder.baseUrl("http://match-service").build();
    }

    public void createOngoingMatch(List<OngoingMatchRequest> request) {
        restClient
                .post()
                .uri("/api/matches/ongoing")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
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
