package net.chess_platform.common.domain_events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import net.chess_platform.common.domain_events.config.DomainEventConfig;
import net.chess_platform.common.domain_events.job.RetryPendingEventsJob;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.domain_events.service.DomainEventSubscriptionRegistry;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ RetryPendingEventsJob.class, DomainEventService.class, DomainEventSubscriptionRegistry.class,
        DomainEventConfig.class })
public @interface EnableDomainEvents {

}
