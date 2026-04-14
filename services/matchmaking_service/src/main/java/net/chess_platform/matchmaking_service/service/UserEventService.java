package net.chess_platform.matchmaking_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.matchmaking_service.exception.UserAlreadyExistsException;
import net.chess_platform.matchmaking_service.model.MatchmakingUser;
import net.chess_platform.matchmaking_service.repository.MatchmakingUserRepository;

@Service
public class UserEventService {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private final UserEventWriter userEventWriter;

    private final DomainEventService eventService;

    public UserEventService(UserEventWriter userEventWriter,
            DomainEventService eventService) {
        this.userEventWriter = userEventWriter;
        this.eventService = eventService;
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

        private MatchmakingUserRepository matchmakingUserRepository;

        public UserEventWriter(MatchmakingUserRepository matchmakingUserRepository) {
            this.matchmakingUserRepository = matchmakingUserRepository;
        }

        @Transactional
        public void write(UserCreatedEvent e) {
            try {
                var user = new MatchmakingUser();
                user.setId(e.getData().userId());

                matchmakingUserRepository.saveAndFlush(user);
            } catch (DataIntegrityViolationException ex) {
                throw new UserAlreadyExistsException();
            }

        }
    }
}
