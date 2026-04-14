package net.chess_platform.common.domain_events.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.chess_platform.common.domain_events.broker.AckEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.model.AckedDomainEvent;
import net.chess_platform.common.domain_events.model.DomainEventAck;
import net.chess_platform.common.domain_events.model.DomainEventData;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class DomainEventService {

    public static record DomainEventSavedEvent(DomainEvent<?> event, List<IEventPublisherService> recipients) {
    }

    public static record DomainEventRetryEvent(DomainEvent<?> event, List<String> recipients) {
    }

    public static record DomainEventAckedEvent(DomainEvent<?> event, String serviceName) {

    }

    private EventDBWriter writer;

    private DomainEventSubscriptionRegistry registry;

    public DomainEventService(EventDBWriter writer, DomainEventSubscriptionRegistry registry) {
        this.writer = writer;
        this.registry = registry;
    }

    public void publish(DomainEvent<?> e) {
        var subscriptionsWithAck = registry.getSubscriptionsWithAckFor(e.getType());
        writer.save(e, subscriptionsWithAck);

        var subscriptionsWithoutAck = registry.getSubscriptionsWithoutAckFor(e.getType());
        publish(e, subscriptionsWithoutAck);
    }

    public void reDispatchPendingEvents() {
        writer.retryPending();
    }

    public void confirmAck(AckEvent e) {
        writer.confirmAck(e);
    }

    public void ack(DomainEvent<?> e, String serviceName) {
        try {
            writer.ack(e, serviceName);
        } catch (UnexpectedRollbackException ex) {
        }
    }

    private void publish(DomainEvent<?> e, List<IEventPublisherService> services) {
        for (var service : services) {
            service.publish(e);
        }
    }

    private void publish(AckEvent e, List<IEventPublisherService> services) {
        for (var service : services) {
            service.publish(e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void onSave(DomainEventSavedEvent e) {
        publish(e.event(), e.recipients());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    protected void onRetry(DomainEventRetryEvent e) {
        var subscriptionsWithAck = registry.getSubscriptionsWithAckFor(e.event().getType());
        var services = new ArrayList<IEventPublisherService>();
        for (var service : subscriptionsWithAck) {
            for (var recipient : e.recipients()) {
                if (recipient.equals(service.getName())) {
                    services.add(service);
                }
            }
        }

        publish(e.event(), services);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    protected void onAck(DomainEventAckedEvent e) {
        var event = e.event();
        var ackEvent = new AckEvent(event, e.serviceName());
        publish(ackEvent, registry.getAckSubscriptionsFor(event.getType()));
    }

    @Service
    protected static class EventDBWriter {

        private ApplicationEventPublisher publisher;

        @PersistenceContext
        private EntityManager em;

        private ObjectMapper objectMapper;

        public EventDBWriter(ApplicationEventPublisher publisher,
                ObjectMapper objectMapper) {
            this.publisher = publisher;
            this.objectMapper = objectMapper;
        }

        @Transactional
        public void save(DomainEvent<?> e, List<IEventPublisherService> recipients) {
            if (recipients == null || recipients.isEmpty()) {
                return;
            }

            try {
                var event = new DomainEventData();
                event.setId(e.getId());
                event.setType(e.getType());
                event.setCategory(e.getCategory());
                event.setData(objectMapper.writeValueAsString(e));

                var acks = new ArrayList<DomainEventAck>();
                for (var service : recipients) {
                    var r = new DomainEventAck();
                    r.setEvent(event);
                    r.setServiceName(service.getName());
                    acks.add(r);
                }

                em.persist(event);
                for (var ack : acks) {
                    em.persist(ack);
                }

                publisher.publishEvent(new DomainEventSavedEvent(e, recipients));
            }

            catch (JacksonException ex) {

            }

        }

        @Transactional
        public void retryPending() {
            var pendingAcks = em.createQuery(
                    "SELECT a FROM DomainEventAck a WHERE a.status = 'PENDING'",
                    DomainEventAck.class)
                    .getResultList();

            try {
                for (var ack : pendingAcks) {
                    var event = ack.getEvent();
                    var e = objectMapper.readValue(event.getData(), DomainEvent.EVENT_TYPES.get(event.getType()));

                    var recipients = new ArrayList<String>();

                    ack.setLastSentAt(OffsetDateTime.now());
                    em.merge(ack);
                    recipients.add(ack.getServiceName());

                    // eventAckRepository.saveAll(eventAcks);
                    publisher.publishEvent(new DomainEventRetryEvent(e, recipients));
                }

            } catch (JacksonException ex) {
            }
        }

        @Transactional
        public void ack(DomainEvent<?> e, String serviceName) {
            try {
                var ackedEvent = new AckedDomainEvent();
                ackedEvent.setId(e.getId());
                ackedEvent.setType(e.getType());
                ackedEvent.setCategory(e.getCategory());
                ackedEvent.setData(objectMapper.writeValueAsString(e));

                em.persist(ackedEvent);
                em.flush();

                publisher.publishEvent(new DomainEventAckedEvent(e, serviceName));
            } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
                publisher.publishEvent(new DomainEventAckedEvent(e, serviceName));
            } catch (JacksonException ex) {
            }
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void confirmAck(AckEvent e) {
            em.createQuery(
                    "UPDATE DomainEventAck a SET a.status = 'ACKED', a.ackedAt = :now WHERE a.event.id = :eventId and a.serviceName = :serviceName")
                    .setParameter("eventId", e.getAckedId())
                    .setParameter("serviceName", e.getServiceName())
                    .setParameter("now", OffsetDateTime.now()).executeUpdate();
        }

    }

}
