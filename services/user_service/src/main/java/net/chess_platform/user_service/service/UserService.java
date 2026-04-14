package net.chess_platform.user_service.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.integration.KeycloakProxy;
import net.chess_platform.user_service.mapper.UserMapper;
import net.chess_platform.user_service.model.User;
import net.chess_platform.user_service.repository.UserRepository;

@Service
public class UserService {

    private final KeycloakProxy keycloak;

    private final UserMapper mapper;

    private final UserWriter userWriter;

    public UserService(KeycloakProxy keyCloak, UserWriter userWriter, UserMapper mapper) {
        this.keycloak = keyCloak;
        this.userWriter = userWriter;
        this.mapper = mapper;
    }

    public void syncUnsyncedUsers() {
        var unsynced = keycloak.getUnsyncedUsers();
        for (var kcUser : unsynced) {
            try {
                userWriter.create(mapper.toModel(kcUser));
                kcUser.setSynced("true");
                keycloak.updateUser(kcUser);
            } catch (DataIntegrityViolationException e) {
                kcUser.setSynced("true");
                keycloak.updateUser(kcUser);
            }
        }
    }

    public void process(KeycloakUserVerifiedMessage m) {
        try {
            userWriter.create(mapper.toModel(m));
            var kcUser = mapper.toKeycloakUserRepresentation(m);
            kcUser.setSynced("true");
            keycloak.updateUser(kcUser);
        } catch (DataIntegrityViolationException e) {
            var kcUser = mapper.toKeycloakUserRepresentation(m);
            kcUser.setSynced("true");
            keycloak.updateUser(kcUser);
        }
    }

    public User create(User user) {
        var id = keycloak.createUser(mapper.toKeycloakUserRepresentation(user));
        user.setId(id);
        return userWriter.create(user);
    }

    public void update(User user) {
        keycloak.updateUser(mapper.toKeycloakUserRepresentation(user));
        userWriter.update(user);
    }

    public void delete(UUID id) {
        keycloak.deleteUser(id.toString());
    }

    @Service
    public static class UserWriter {

        private UserRepository userRepository;

        private DomainEventService domainEventService;

        public UserWriter(UserRepository userRepository, DomainEventService domainEventService) {
            this.userRepository = userRepository;
            this.domainEventService = domainEventService;
        }

        @Transactional
        public User create(User user) {
            var savedUser = userRepository.save(user);
            var event = new UserCreatedEvent(savedUser.getId(), savedUser.getUsername(), savedUser.getDisplayName(),
                    savedUser.getEmail(), savedUser.getAvatar());
            domainEventService.publish(event);
            return savedUser;
        }

        @Transactional
        public User update(User user) {
            var savedUser = userRepository.save(user);
            var event = new UserUpdatedEvent(savedUser.getId(), savedUser.getUsername(), savedUser.getDisplayName(),
                    savedUser.getEmail(), savedUser.getAvatar());
            domainEventService.publish(event);
            return savedUser;
        }
    }
}
