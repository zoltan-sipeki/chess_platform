package net.chess_platform.match_service.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PlayerMmrDto;
import net.chess_platform.match_service.exception.EntityNotFoundException;
import net.chess_platform.match_service.mapper.PlayerMmrMapper;
import net.chess_platform.match_service.permission.PermissionService;
import net.chess_platform.match_service.permission.PermissionService.Action;
import net.chess_platform.match_service.repository.PlayerMmrRepository;

@Service
public class PlayerMmrService {

    private final PlayerMmrRepository playerMmrRepository;

    private final PlayerMmrMapper playerMmrMapper;

    private final PermissionService permissionService;

    public PlayerMmrService(PlayerMmrRepository playerMmrRepository, PlayerMmrMapper playerMmrMapper,
            PermissionService permissionService) {
        this.playerMmrRepository = playerMmrRepository;
        this.playerMmrMapper = playerMmrMapper;
        this.permissionService = permissionService;
    }

    public PlayerMmrDto find(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.MMR_QUERY, user, Map.of("userId", userId));
        var mmr = playerMmrRepository.findOne(auth).orElseThrow(() -> new EntityNotFoundException());
        return playerMmrMapper.toDto(mmr);
    }

}
