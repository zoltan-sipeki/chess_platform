package net.chess_platform.user_service.service;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserEventData;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.keycloak.event.KeycloakUserUpdatedEvent;
import net.chess_platform.keycloak.event.KeycloakUserVerifiedEvent;
import net.chess_platform.user_service.dto.UserDto;
import net.chess_platform.user_service.dto.UserSearchResultDto;
import net.chess_platform.user_service.exception.EntityNotFoundException;
import net.chess_platform.user_service.exception.InvalidUserException;
import net.chess_platform.user_service.integration.KeycloakProxy;
import net.chess_platform.user_service.mapper.UserMapper;
import net.chess_platform.user_service.model.User;
import net.chess_platform.user_service.repository.UserRepository;

@Service
public class UserService {

    private final KeycloakProxy keycloak;

    private final UserMapper mapper;

    private final DomainEventService eventService;

    private final UserRepository userRepository;

    private final UserWriter writer = this.new UserWriter();

    public UserService(KeycloakProxy keyCloak, UserMapper mapper,
            DomainEventService eventService, UserRepository userRepository) {
        this.keycloak = keyCloak;
        this.mapper = mapper;
        this.eventService = eventService;
        this.userRepository = userRepository;
    }

    public void syncUnsyncedUsers() {
        var unsynced = keycloak.getUnsyncedUsers();
        for (var kcr : unsynced) {
            try {
                writer.create(mapper.toModel(kcr));
                kcr.setSynced("true");
                kcr.setUpdated("false");
                keycloak.updateUser(kcr);
            } catch (DataIntegrityViolationException e) {
                if (kcr.getUpdated().equals("true")) {
                    writer.update(mapper.toUpdate(kcr));
                }
                kcr.setSynced("true");
                kcr.setUpdated("false");
                keycloak.updateUser(kcr);
            }
        }
    }

    public void process(KeycloakUserVerifiedEvent e) {
        var kcu = e.getPayload();
        try {
            writer.create(mapper.toModel(kcu));
            var kcr = mapper.toKeycloakUserRepresentation(kcu);
            kcr.setSynced("true");
            kcr.setUpdated("false");
            keycloak.updateUser(kcr);
        } catch (DataIntegrityViolationException ex) {
            var kcr = mapper.toKeycloakUserRepresentation(kcu);
            kcr.setSynced("true");
            kcr.setUpdated("false");
            keycloak.updateUser(kcr);
        }

    }

    public void process(KeycloakUserUpdatedEvent e) {
        var kcu = e.getPayload();
        User u = null;
        try {
            u = writer.update(mapper.toUpdate(kcu));
            var kcr = mapper.toKeycloakUserRepresentation(u);
            kcr.setSynced("true");
            kcr.setUpdated("false");
            keycloak.updateUser(kcr);
        } catch (InvalidUserException ex) {
            var kcr = mapper.toKeycloakUserRepresentation(u);
            kcr.setSynced("true");
            kcr.setUpdated("false");
            keycloak.updateUser(kcr);
        }
    }

    public UserSearchResultDto findByDisplayNamePrefix(String prefix, Pageable pageable) {
        var result = writer.findByDisplayNamePrefix(prefix, pageable);
        return new UserSearchResultDto(result.getTotalElements(), mapper.toDtoList(result.getContent()));
    }

    public void delete(UUID id) {
        keycloak.deleteUser(id.toString());
    }

    public UserDto findById(UUID id) {
        var user = writer.findById(id);
        return mapper.toDto(user);
    }

    public UserDto update(User.Update update, CurrentUser currentUser) {
        if (update.getId() == null) {
            throw new InvalidUserException();
        }

        var u = writer.update(update);
        return mapper.toDto(u);
    }

    private class UserWriter {

        public User findById(UUID userId) {
            return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException());
        }

        public Page<User> findByDisplayNamePrefix(String prefix, Pageable pageable) {
            return userRepository.findByDisplayNamePrefix(prefix, pageable);
        }

        @Transactional
        public User create(User user) {
            user.setDisplayName(user.getUsername());

            userRepository.save(user);

            var payload = new UserEventData.Builder(user.getId()).displayName(user.getDisplayName())
                    .avatar(user.getAvatar()).build();
            var event = new UserCreatedEvent(payload);
            eventService.publish(event);

            return user;
        }

        @Transactional
        public User update(User.Update user) {
            var payload = new UserEventData.Builder(user.getId());

            boolean sendEvent = false;

            var avatar = user.getAvatar();
            if (avatar != null) {
                payload.avatar(avatar);
                sendEvent = true;
            }

            var displayName = user.getDisplayName();
            if (displayName != null) {
                payload.displayName(displayName);
                sendEvent = true;
            }

            var u = userRepository.updateAndFetch(user);

            if (u != null && sendEvent) {
                var event = new UserUpdatedEvent(payload.build());
                eventService.publish(event);
            }

            return u;
        }
    }
}
