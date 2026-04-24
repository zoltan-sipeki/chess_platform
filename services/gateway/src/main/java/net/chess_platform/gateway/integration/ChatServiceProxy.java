package net.chess_platform.gateway.integration;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatServiceProxy {

    private final RestClient restClient;

    public static record ChannelDto(UUID id, String name, String type, List<UserDto> members) {
    }

    public static record UserDto(UUID id, String displayName, String avatar) {
    }

    public ChatServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://chat_service").build();
    }

    public List<UserDto> getFriends(String userId) {
        return restClient.get().uri("/api/friends?userId={userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserDto>>() {
                });
    }

    public List<UserDto> getFriends() {
        return restClient.get().uri("/api/friends")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserDto>>() {
                });
    }

    public List<ChannelDto> getChannels() {
        return restClient.get().uri("/api/channels")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ChannelDto>>() {
                });
    }
}
