package net.chess_platform.common.domain_events.service;

public interface IDomainEventConfigurer {

    void configure(DomainEventSubscriptionRegistry registry);

    void configure(DomainEventService service);
}
