package net.chess_platform.chess_service.coordinator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.netflix.discovery.EurekaClient;

import net.chess_platform.chess_service.coordinator.message.MatchmakingToken;
import net.chess_platform.chess_service.exception.InvalidMatchmakingTokenException;

@Component
public class MatchmakingTokenVerifier {

    private final MatchmakingTokenParser mmTokenParser;

    private final EurekaClient eurekaClient;

    private volatile UUID instanceUuid;

    public MatchmakingTokenVerifier(MatchmakingTokenParser mmTokenParser, EurekaClient eurekaClient) {
        this.mmTokenParser = mmTokenParser;
        this.eurekaClient = eurekaClient;
    }

    public MatchmakingToken verify(String token, UUID userId) {
        var decoded = mmTokenParser.verifyMatchmakingToken(token);
        if (instanceUuid == null) {
            instanceUuid = UUID
                    .fromString(eurekaClient.getApplicationInfoManager().getInfo().getMetadata().get("uuid"));
        }

        if (!decoded.getPlayerId().equals(userId)) {
            throw new InvalidMatchmakingTokenException("User ID does not match");
        }

        if (!decoded.getTarget().equals(instanceUuid)) {
            throw new InvalidMatchmakingTokenException("Instance UUID does not match");
        }

        return decoded;
    }
}
