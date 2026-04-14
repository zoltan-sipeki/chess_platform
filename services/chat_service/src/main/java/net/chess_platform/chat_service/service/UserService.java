package net.chess_platform.chat_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.chat_service.model.Privacy.Restriction;
import net.chess_platform.chat_service.model.Privacy.Restriction.Resource;
import net.chess_platform.chat_service.model.Privacy.Restriction.Setting;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Service
public class UserService {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private DomainEventService eventService;

    private UserRepository userRepository;

    private PrivacyRepository privacyRepository;

    public UserService(DomainEventService eventService, UserRepository userRepository,
            PrivacyRepository privacyRepository) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.privacyRepository = privacyRepository;
    }

    public void process(UserCreatedEvent e) {
        var eventUser = e.getData();

        var user = new User();
        user.setId(eventUser.userId());
        user.setDisplayName(eventUser.displayName());
        user.setAvatar(eventUser.avatar());

        userRepository.save(user);

        var privacy = new Privacy();
        privacy.setUserId(user.getId());
        privacy.addRestriction(new Restriction(Resource.FRIENDS, Setting.PUBLIC));

        privacyRepository.save(privacy);

        eventService.ack(e, SERVICE_NAME);
    }

}
