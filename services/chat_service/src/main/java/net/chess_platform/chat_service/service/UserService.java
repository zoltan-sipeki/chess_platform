package net.chess_platform.chat_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.mapper.UserMapper;
import net.chess_platform.chat_service.model.Privacy;
import net.chess_platform.chat_service.model.Privacy.Restriction;
import net.chess_platform.chat_service.model.Privacy.Restriction.Resource;
import net.chess_platform.chat_service.model.Privacy.Restriction.Setting;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.chat_service.repository.PrivacyRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;

@Service
public class UserService {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private DomainEventService eventService;

    private UserRepository userRepository;

    private PrivacyRepository privacyRepository;

    private final UserMapper mapper;

    public UserService(DomainEventService eventService, UserRepository userRepository,
            PrivacyRepository privacyRepository, UserMapper mapper) {
        this.eventService = eventService;
        this.userRepository = userRepository;
        this.privacyRepository = privacyRepository;
        this.mapper = mapper;
    }

    public void process(UserCreatedEvent e) {
        var eventUser = e.getData();

        var user = new User();
        user.setId(eventUser.getId());
        user.setDisplayName(eventUser.getDisplayName());
        user.setAvatar(eventUser.getAvatar());

        userRepository.save(user);

        var privacy = new Privacy();
        privacy.setUserId(user.getId());
        privacy.addRestriction(new Restriction(Resource.FRIENDS, Setting.PUBLIC));

        privacyRepository.save(privacy);

        eventService.ack(e, SERVICE_NAME);
    }

    public void process(UserUpdatedEvent e) {
        var u = e.getData();
        var update = mapper.toUpdate(u);

        userRepository.update(update);
        eventService.ack(e, SERVICE_NAME);
    }

}
