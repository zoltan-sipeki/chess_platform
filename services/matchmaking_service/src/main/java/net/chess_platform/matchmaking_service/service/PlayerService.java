package net.chess_platform.matchmaking_service.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.matchmaking_service.model.Player;
import net.chess_platform.matchmaking_service.repository.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final DomainEventService eventService;

    public PlayerService(PlayerRepository playerRepository, DomainEventService eventService) {
        this.playerRepository = playerRepository;
        this.eventService = eventService;
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            if (eventService.exists(e)) {
                return;
            }

            var u = e.getData();
            var user = new Player();
            user.setId(u.id());
            user.setDisplayName(u.displayName());
            user.setAvatar(u.avatar());

            playerRepository.saveAndFlush(user);

            eventService.ack(e);
        } catch (DataIntegrityViolationException ex) {
            eventService.ack(e);
        }

    }

    @Transactional
    public void process(UserUpdatedEvent e) {
        var d = e.getData();

        var update = new Player.Update();
        update.setDisplayName(d.displayName());
        update.setAvatar(d.avatar());

        playerRepository.update(d.id(), update);

        eventService.ack(e);
    }
}
