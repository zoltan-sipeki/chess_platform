package net.chess_platform.common.domain_events.job;

import java.time.Duration;
import java.time.Instant;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import net.chess_platform.common.domain_events.DomainEventProperties;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Component
public class RetryPendingEventsJob {

    private DomainEventProperties properties;

    private DomainEventService eventService;

    private TaskScheduler taskScheduler;

    public RetryPendingEventsJob(DomainEventProperties properties, TaskScheduler taskScheduler,
            DomainEventService eventService) {
        this.properties = properties;
        this.taskScheduler = taskScheduler;
        this.eventService = eventService;
    }

    @PostConstruct
    public void init() {
        long ms = properties.getRetryIntervalMs();
        taskScheduler.scheduleAtFixedRate(() -> eventService.reDispatchPendingEvents(),
                Instant.now().plusMillis(ms), Duration.ofMillis(ms));
    }

}
