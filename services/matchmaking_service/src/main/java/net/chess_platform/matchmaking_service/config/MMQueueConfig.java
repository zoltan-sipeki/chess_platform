package net.chess_platform.matchmaking_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.matchmaking_service.mmqueue.MMQueue;
import net.chess_platform.matchmaking_service.mmqueue.Match;
import net.chess_platform.matchmaking_service.repository.MatchmakingUserRepository;

@Configuration
public class MMQueueConfig {

    @Bean
    public MMQueue unrankedQueue(MatchmakingUserRepository matchmakingUserRepository) {
        return new MMQueue(Match.Type.UNRANKED, matchmakingUserRepository);
    }

    @Bean
    public MMQueue rankedQueue(MatchmakingUserRepository matchmakingUserRepository) {
        return new MMQueue(Match.Type.RANKED, matchmakingUserRepository);
    }

}
