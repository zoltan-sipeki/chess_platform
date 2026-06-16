package net.chess_platform.relay_service.integration;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver.principal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.service.IEventPublisherService;

@Service
public class ChatServiceProxy implements IEventPublisherService {

    @Value("${rabbitmq-messaging.routing-key.chat-service}")
    private String ROUTING_KEY;

    private final RestClient restClient;

    private final RabbitTemplate relayEvents;

    public ChatServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder,
            @Qualifier("relayEventsRabbitTemplate") RabbitTemplate relayEvents) {
        this.restClient = builder.baseUrl("lb://chat-service").build();
        this.relayEvents = relayEvents;
    }

    public List<UUID> getContacts(UUID userId) {
        var response = restClient.get()
                .uri(uri -> uri.path("/api/users/{userId}/contacts").build(Map.of("userId", userId)))
                .attributes(clientRegistrationId("keycloak"))
                .attributes(principal("relay-service"))
                .retrieve()
                .body(new ParameterizedTypeReference<List<UUID>>() {
                });
        return response;
    }

    @Override
    public String getName() {
        return ROUTING_KEY;
    }

    @Override
    public void publish(DomainEvent<?> e) {
        relayEvents.convertAndSend(ROUTING_KEY, e);
    }

    @Override
    public void publish(AckEvent e) {
        
    }

}
