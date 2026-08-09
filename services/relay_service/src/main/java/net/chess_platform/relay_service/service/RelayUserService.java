package net.chess_platform.relay_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.broker.relay.RelayDisconnectEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.relay_service.exception.InvalidUserException;
import net.chess_platform.relay_service.integration.ChatServiceProxy;
import net.chess_platform.relay_service.model.RelayUser;
import net.chess_platform.relay_service.model.RelayUser.Presence;
import net.chess_platform.relay_service.repository.RelayUserRepository;

@Service
public class RelayUserService {

    private final RelayUserRepository relayUserRepository;

    private final DomainEventService eventService;

    private final ChatServiceProxy chatService;

    public RelayUserService(RelayUserRepository relayUserRepository, DomainEventService eventService,
            ChatServiceProxy chatService) {
        this.relayUserRepository = relayUserRepository;
        this.eventService = eventService;
        this.chatService = chatService;
    }

    public Presence getPreferredPresence(UUID userId) {
        var user = relayUserRepository.findById(userId).orElseThrow(() -> new InvalidUserException());
        return user.getPreferredPresence();
    }

    public void broadcastPresence(UUID userId) {
        List<UUID> contacts = new ArrayList<>();

        var presence = getPreferredPresence(userId);
        if (getPreferredPresence(userId) != Presence.OFFLINE) {
            contacts = chatService.getContacts(userId);
        }

        contacts.add(userId);
        var event = new PresenceChangedEvent(contacts,
                new PresenceChangedEvent.Payload(userId,
                        PresenceChangedEvent.Presence.valueOf(presence.name())));
        eventService.publish(event);
    }

    public void boardcastPresence(UUID userId, Presence presence) {
        if (getPreferredPresence(userId) == Presence.OFFLINE) {
            return;
        }
        var contacts = chatService.getContacts(userId);
        var event = new PresenceChangedEvent(contacts,
                new PresenceChangedEvent.Payload(userId, PresenceChangedEvent.Presence.valueOf(presence.name())));
        eventService.publish(event);
        eventService.publish(new RelayDisconnectEvent(userId));
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            if (eventService.exists(e)) {
                return;
            }

            var user = new RelayUser();
            user.setId(e.getData().id());

            relayUserRepository.saveAndFlush(user);

            eventService.ack(e);
        } catch (DataIntegrityViolationException ex) {
            eventService.ack(e);
        }
    }

    public void updatePreferredPresence(Presence presence, CurrentUser currentUser) {
        relayUserRepository.updatePreferredPresence(currentUser.id(), presence);
        var contacts = chatService.getContacts(currentUser.id());
        contacts.add(currentUser.id());
        var event = new PresenceChangedEvent(contacts,
                new PresenceChangedEvent.Payload(currentUser.id(),
                        PresenceChangedEvent.Presence.valueOf(presence.name())));
        eventService.publish(event);
    }
}
