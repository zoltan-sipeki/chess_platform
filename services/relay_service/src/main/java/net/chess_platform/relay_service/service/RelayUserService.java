package net.chess_platform.relay_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.relay_service.integration.ChatServiceProxy;
import net.chess_platform.relay_service.model.RelayUser.Presence;
import net.chess_platform.relay_service.repository.RelayUserRepository;

@Service
public class RelayUserService {

    private RelayUserRepository relayUserRepository;

    private DomainEventService eventService;

    private ChatServiceProxy chatService;

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
}
