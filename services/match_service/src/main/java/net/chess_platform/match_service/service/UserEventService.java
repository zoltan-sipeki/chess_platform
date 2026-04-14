package net.chess_platform.match_service.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.match_service.exception.UserAlreadyExistsException;
import net.chess_platform.match_service.model.MatchUser;
import net.chess_platform.match_service.model.PlayerMmr;
import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.repository.MatchUserRepository;
import net.chess_platform.match_service.repository.PlayerMmrRepository;
import net.chess_platform.match_service.repository.PrivacySettingRepository;

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

        private final MatchUserRepository matchUserRepository;

        private final PlayerMmrRepository playerMmrRepository;

        private final PrivacySettingRepository privacySettingRepository;

        public UserEventWriter(MatchUserRepository matchUserRepository, PlayerMmrRepository playerMmrRepository,
                PrivacySettingRepository privacySettingRepository) {
            this.matchUserRepository = matchUserRepository;
            this.playerMmrRepository = playerMmrRepository;
            this.privacySettingRepository = privacySettingRepository;
        }

        @Transactional
        public void write(UserCreatedEvent e) {
            try {
                var user = new MatchUser();
                var data = e.getData();
                user.setId(data.userId());
                user.setDisplayName(data.displayName());
                user.setAvatar(data.avatar());

                matchUserRepository.saveAndFlush(user);

                var mmr = new PlayerMmr();
                mmr.setUser(user);

                playerMmrRepository.save(mmr);

                var privacySettings = new ArrayList<PrivacySetting>();
                for (var setting : PrivacySetting.Resource.values()) {
                    var ps = new PrivacySetting();
                    ps.setUser(user);
                    ps.setResource(setting);
                    privacySettings.add(ps);
                }

                privacySettingRepository.saveAll(privacySettings);

            } catch (DataIntegrityViolationException ex) {
                throw new UserAlreadyExistsException();
            }
        }
    }
}
