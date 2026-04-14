package net.chess_platform.chat_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.SendFriendRequestRequest;
import net.chess_platform.chat_service.dto.UpdateFriendRequest;
import net.chess_platform.chat_service.model.Notification.FriendRequestDetails;
import net.chess_platform.chat_service.service.FriendService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/friendRequests")
public class FriendRequestController {

    private FriendService friendService;

    public FriendRequestController(FriendService friendRequestService) {
        this.friendService = friendRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void create(@RequestBody SendFriendRequestRequest dto, CurrentUser user) {
        friendService.createFriendRequest(dto.receiverId(), user);
    }

    @PatchMapping("/{friendRequestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID friendRequestId, @RequestBody UpdateFriendRequest dto,
            CurrentUser user) {
        friendService.updateFriendRequestStatus(friendRequestId, FriendRequestDetails.Status.valueOf(dto.status()), user);
    }
}
