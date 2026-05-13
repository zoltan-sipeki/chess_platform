package net.chess_platform.matchmaking_service.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.matchmaking_service.exception.UserAlreadyExistsException;
import net.chess_platform.matchmaking_service.model.Player;
import net.chess_platform.matchmaking_service.repository.PlayerRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public void process(UserCreatedEvent e) {
        try {
            var user = new Player();
            user.setId(e.getData().userId());

            playerRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException();
        }

    }
}
