package net.chess_platform.common.domain_events.service;

import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;

public interface IEventPublisherService {

    public String getName();

    public void publish(DomainEvent<?> e);

    public void publish(AckEvent e);
}
