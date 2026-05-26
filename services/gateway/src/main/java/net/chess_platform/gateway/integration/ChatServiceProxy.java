package net.chess_platform.gateway.integration;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import net.chess_platform.common.dto.chat.UserDto;

@Service
public class ChatServiceProxy {

    private final RestClient restClient;

    public static record ChannelDto(UUID id, String name, String type, List<UserDto> members) {
    }

    public static record FriendListDto(long total, List<UserDto> friends) {
    }

    public static record RelationshipSearchDto(List<String> ids) {

    }

    public static record RelationshipDto(String relationship) {
    }

    public static record PrivacyDto(String friends) {
    }

    public ChatServiceProxy(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://chat-service").build();
    }

    public PrivacyDto getPrivacySettings() {
        return restClient.get().uri("/api/privacy")
                .retrieve()
                .body(PrivacyDto.class);
    }

    public String getRelationship(String userId) {
        var response = restClient.post().uri("/api/relationships/search")
                .body(new RelationshipSearchDto(List.of(userId)))
                .retrieve()
                .body(RelationshipDto.class);
        return response.relationship();
    }

    public FriendListDto getFriends(String userId) {
        return restClient.get().uri("/api/friends?userId={userId}&size=10&sort=displayName,asc", userId)
                .retrieve()
                .body(FriendListDto.class);
    }

    public FriendListDto getFriends() {
        return restClient.get().uri("/api/friends")
                .retrieve()
                .body(FriendListDto.class);
    }

    public List<ChannelDto> getChannels() {
        return restClient.get().uri("/api/channels")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ChannelDto>>() {
                });
    }
}
