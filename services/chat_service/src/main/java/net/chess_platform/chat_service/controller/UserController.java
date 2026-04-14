package net.chess_platform.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.ContactsDto;
import net.chess_platform.chat_service.service.FriendService;
import net.chess_platform.chat_service.service.RelationShipService;
import net.chess_platform.common.dto.chat.UserDto;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private FriendService friendService;

    private RelationShipService relationShipService;

    public UserController(FriendService friendService, RelationShipService relationShipService) {
        this.friendService = friendService;
        this.relationShipService = relationShipService;
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getFriends(@PathVariable UUID userId, @RequestParam(defaultValue = "50") int limit,
            CurrentUser currentUser) {
        return friendService.findAll(userId, currentUser);
    }

    @DeleteMapping("/me/friends/{friendId}")
    public void unfriend(@PathVariable UUID friendId, CurrentUser currentUser) {
        friendService.unfriend(friendId, currentUser);
    }

    @GetMapping("/me/friends")
    public List<UserDto> getMyFriends(CurrentUser currentUser) {
        return friendService.findAll(currentUser.id(), currentUser);
    }

    @GetMapping("/{userId}/contacts")
    public ContactsDto getContacts(@PathVariable UUID userId, CurrentUser currentUser) {
        return relationShipService.findContacts(userId, currentUser);
    }

}
