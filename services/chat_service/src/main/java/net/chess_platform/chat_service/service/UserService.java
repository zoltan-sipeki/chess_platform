package net.chess_platform.chat_service.service;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.mapper.UserMapper;
import net.chess_platform.chat_service.model.NotificationMetadata;
import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.chat_service.model.Privacy.Restriction;
import net.chess_platform.chat_service.model.Privacy.Restriction.Resource;
import net.chess_platform.chat_service.model.Privacy.Restriction.Setting;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.chat_service.model.User.Activity;
import net.chess_platform.chat_service.model.User.Presence;
import net.chess_platform.chat_service.repository.NotificationMetadataRepository;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.ActivityChangedEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Service
public class UserService {

    private final DomainEventService eventService;

    private final UserRepository userRepository;

    private final PrivacyRepository privacyRepository;

    private final NotificationMetadataRepository notificationMetadataRepository;

    private final UserMapper mapper;

    public UserService(DomainEventService eventService, UserRepository userRepository,
            PrivacyRepository privacyRepository,
            NotificationMetadataRepository notificationMetadataRepository, UserMapper mapper) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.privacyRepository = privacyRepository;
        this.notificationMetadataRepository = notificationMetadataRepository;
        this.mapper = mapper;
    }

    public void process(UserCreatedEvent e) {
        if (eventService.exists(e)) {
            return;
        }

        var eventUser = e.getData();

        var user = new User();
        user.setId(eventUser.id());
        user.setDisplayName(eventUser.displayName());
        user.setAvatar(eventUser.avatar());

        userRepository.save(user);

        var privacy = new Privacy();
        privacy.setUserId(user.getId());
        privacy.addRestriction(new Restriction(Resource.FRIENDS, Setting.PUBLIC));

        privacyRepository.save(privacy);

        var notificationMetadata = new NotificationMetadata();
        notificationMetadata.setReceiver(user.getId());
        
        notificationMetadataRepository.save(notificationMetadata);

        eventService.ack(e);
    }

    public void process(UserUpdatedEvent e) {
        if (eventService.exists(e)) {
            return;
        }
        var d = e.getData();
        var update = mapper.toUpdate(d);

        userRepository.update(d.id(), update);
        
        eventService.ack(e);
    }

    public void process(PresenceChangedEvent e) {
        var d = e.getData();
        var update = new User.Update();
        update.setPresence(Presence.valueOf(d.presence().name()));

        userRepository.update(d.userId(), update);
    }

    public void process(ActivityChangedEvent e) {
        var d = e.getData();
        var update = new User.Update();
        update.setActivity(Activity.valueOf(d.activity().name()));
        userRepository.update(d.userId(), update);
    }
}
