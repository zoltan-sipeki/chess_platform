package net.chess_platform.common.domain_events.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.DomainEvent;

@Component
public class SubscriptionRegistry {

    private Map<DomainEvent.Type, List<IEventPublisherService>> subscriptionsWithAck = new HashMap<>();

    private Map<DomainEvent.Type, List<IEventPublisherService>> subscriptionsWithoutAck = new HashMap<>();

    private Map<DomainEvent.Type, List<IEventPublisherService>> acks = new HashMap<>();

    public void registerAck(DomainEvent.Type eventType, IEventPublisherService service) {
        synchronized (acks) {
            acks.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
        }
    }

    public void registerSubscription(DomainEvent.Type eventType, IEventPublisherService service, boolean ackRequired) {
        if (ackRequired) {
            synchronized (subscriptionsWithAck) {
                subscriptionsWithAck.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
            }
        } else {
            synchronized (subscriptionsWithoutAck) {
                subscriptionsWithoutAck.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
            }
        }
    }

    public List<IEventPublisherService> getSubscriptionsWithAckFor(DomainEvent.Type eventType) {
        return subscriptionsWithAck.get(eventType);
    }

    public List<IEventPublisherService> getSubscriptionsWithoutAck(DomainEvent.Type eventType) {
        return subscriptionsWithoutAck.get(eventType);
    }
}
