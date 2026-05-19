package net.chess_platform.chat_service.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.FriendListDto;
import net.chess_platform.chat_service.service.FriendService;
import net.chess_platform.common.security.CurrentUser;

@RequestMapping("/api/friends")
@RestController
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    public FriendListDto getAll(@RequestParam Optional<UUID> userId,
            @PageableDefault(size = Integer.MAX_VALUE, sort = "displayName", direction = Direction.ASC) Pageable pageable,
            CurrentUser currentUser) {
        return friendService.findAll(userId.orElse(null), pageable, currentUser);
    }

    @DeleteMapping("/me/{friendId}")
    public void unfriend(@PathVariable UUID friendId, CurrentUser currentUser) {
        friendService.unfriend(friendId, currentUser);
    }
}
