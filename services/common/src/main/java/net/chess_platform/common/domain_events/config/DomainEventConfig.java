package net.chess_platform.common.domain_events.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import net.chess_platform.common.domain_events.DomainEventProperties;

@Configuration
@EnableConfigurationProperties(DomainEventProperties.class)
public class DomainEventConfig {
    @Bean
    public TaskScheduler retryPendingTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
