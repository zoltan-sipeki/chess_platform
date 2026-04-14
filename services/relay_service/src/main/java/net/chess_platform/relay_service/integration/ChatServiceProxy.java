package net.chess_platform.relay_service.integration;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.client.RequestAttributePrincipalResolver.principal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatServiceProxy {

    private RestClient restClient;

    public ChatServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("lb://chat-service").build();
    }

    public static record ContactsDto(UUID userId, List<UUID> contacts) {
    }

    public ContactsDto getContacts(UUID userId) {
        var response = restClient.get()
                .uri(uri -> uri.path("/api/users/{userId}/contacts").build(Map.of("userId", userId)))
                .attributes(clientRegistrationId("keycloak"))
                .attributes(principal("relay-service"))
                .retrieve()
                .body(ContactsDto.class);
        return response;
    }

}
