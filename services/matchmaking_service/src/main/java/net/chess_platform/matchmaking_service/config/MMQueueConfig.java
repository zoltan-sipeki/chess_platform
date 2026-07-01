package net.chess_platform.matchmaking_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.chess_platform.matchmaking_service.mmqueue.MMQueue;
import net.chess_platform.matchmaking_service.mmqueue.Match;

@Configuration
public class MMQueueConfig {

    @Bean
    public MMQueue unrankedQueue() {
        return new MMQueue(Match.Type.UNRANKED);
    }

    @Bean
    public MMQueue rankedQueue() {
        return new MMQueue(Match.Type.RANKED);
    }

}
