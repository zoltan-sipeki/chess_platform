package net.chess_platform.relay_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.relay_service.exception.UserAlreadyExistsException;
import net.chess_platform.relay_service.model.RelayUser;
import net.chess_platform.relay_service.repository.RelayUserRepository;

@Service
public class UserEventService {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private DomainEventService eventService;

    private UserEventWriter userEventWriter;

    public UserEventService(DomainEventService eventService, UserEventWriter userEventWriter) {
        this.eventService = eventService;
        this.userEventWriter = userEventWriter;
    }

    public void process(UserCreatedEvent e) {
        try {
            userEventWriter.write(e);
            eventService.ack(e, SERVICE_NAME);
        } catch (UserAlreadyExistsException ex) {
            eventService.ack(e, SERVICE_NAME);
        }
    }

    @Service
    public static class UserEventWriter {

        private RelayUserRepository relayUserRepository;

        public UserEventWriter(RelayUserRepository relayUserRepository) {
            this.relayUserRepository = relayUserRepository;
        }

        @Transactional
        public void write(UserCreatedEvent e) {
            try {
                var user = new RelayUser();
                user.setId(e.getData().userId());

                relayUserRepository.saveAndFlush(user);
            } catch (DataIntegrityViolationException ex) {
                throw new UserAlreadyExistsException();
            }
        }
    }
}
