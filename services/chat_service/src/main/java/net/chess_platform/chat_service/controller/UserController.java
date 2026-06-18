package net.chess_platform.chat_service.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.FriendListDto;
import net.chess_platform.chat_service.service.FriendService;
import net.chess_platform.chat_service.service.RelationshipService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RelationshipService relationshipService;

    private final FriendService friendService;

    public UserController(RelationshipService relationShipService, FriendService friendService) {
        this.relationshipService = relationShipService;
        this.friendService = friendService;
    }

    @GetMapping("/{userId}/contacts")
    public Set<UUID> getContacts(@PathVariable UUID userId, CurrentUser currentUser) {
        return relationshipService.findContacts(userId, currentUser);
    }

    @GetMapping("/{userId}/friends")
    public FriendListDto getFriends(
            @PathVariable UUID userId,
            @PageableDefault(size = Integer.MAX_VALUE, sort = "displayName", direction = Direction.ASC) Pageable pageable,
            CurrentUser currentUser) {
        return friendService.findAllWithoutPresence(userId, pageable, currentUser);
    }

    @GetMapping("/me/friends")
    public FriendListDto getFriends(
            @PageableDefault(size = Integer.MAX_VALUE, sort = "displayName", direction = Direction.ASC) Pageable pageable,
            CurrentUser currentUser) {
        return friendService.findAllWithPresence(currentUser.id(), pageable, currentUser);
    }

    @DeleteMapping("/me/friends/{friendId}")
    public void unfriend(@PathVariable UUID friendId, CurrentUser currentUser) {
        friendService.unfriend(friendId, currentUser);
    }

}
