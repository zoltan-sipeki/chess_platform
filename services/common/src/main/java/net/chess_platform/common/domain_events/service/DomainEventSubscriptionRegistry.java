package net.chess_platform.common.domain_events.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import net.chess_platform.common.domain_events.broker.DomainEvent;

@Component
public class DomainEventSubscriptionRegistry {

    private Map<DomainEvent.Type, List<IEventPublisherService>> subscriptionsWithAck = new HashMap<>();

    private Map<DomainEvent.Type, List<IEventPublisherService>> subscriptionsWithoutAck = new HashMap<>();

    private Map<DomainEvent.Type, List<IEventPublisherService>> ackSubscriptions = new HashMap<>();

    private IDomainEventSubscriptionConfigurer configurer;

    public DomainEventSubscriptionRegistry(IDomainEventSubscriptionConfigurer configurer) {
        this.configurer = configurer;
    }

    @PostConstruct
    public void init() {
        configurer.configure(this);
    }

    public void registerSubscription(DomainEvent.Type eventType, IEventPublisherService service, boolean ackRequired) {
        if (ackRequired) {
            subscriptionsWithAck.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
        } else {
            subscriptionsWithoutAck.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
        }
    }

    public void registerSubscription(DomainEvent.Type eventType, List<IEventPublisherService> services,
            boolean ackRequired) {
        if (ackRequired) {
            subscriptionsWithAck.computeIfAbsent(eventType, k -> new ArrayList<>()).addAll(services);
        } else {
            subscriptionsWithoutAck.computeIfAbsent(eventType, k -> services).addAll(services);
        }
    }

    public void registerAck(DomainEvent.Type eventType, IEventPublisherService service) {
        ackSubscriptions.computeIfAbsent(eventType, k -> new ArrayList<>()).add(service);
    }

    public void registerAck(DomainEvent.Type eventType, List<IEventPublisherService> services) {
        ackSubscriptions.computeIfAbsent(eventType, k -> services).addAll(services);
    }

    public List<IEventPublisherService> getSubscriptionsWithAckFor(DomainEvent.Type eventType) {
        return subscriptionsWithAck.getOrDefault(eventType, new ArrayList<>());
    }

    public List<IEventPublisherService> getSubscriptionsWithoutAckFor(DomainEvent.Type eventType) {
        return subscriptionsWithoutAck.getOrDefault(eventType, new ArrayList<>());
    }

    public List<IEventPublisherService> getAckSubscriptionsFor(DomainEvent.Type eventType) {
        return ackSubscriptions.get(eventType);
    }

}
