package net.chess_platform.relay_service.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.relay_service.exception.UserAlreadyExistsException;
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

    public void updatePresence(UUID userId, Presence presence) {
        relayUserRepository.updatePresence(userId, presence);
        var contacts = chatService.getContacts(userId);
        eventService.publish(new PresenceChangedEvent(contacts.contacts(), userId, presence.toString()));
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            var user = new RelayUser();
            user.setId(e.getData().id());

            relayUserRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }
    }
}
