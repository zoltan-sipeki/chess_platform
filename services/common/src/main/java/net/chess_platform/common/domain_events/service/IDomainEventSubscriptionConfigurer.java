package net.chess_platform.common.domain_events.service;

public interface IDomainEventSubscriptionConfigurer {

    public void configure(DomainEventSubscriptionRegistry registry);
}
