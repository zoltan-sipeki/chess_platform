package net.chess_platform.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.FriendRequestCreateDto;
import net.chess_platform.chat_service.dto.FriendRequestDto;
import net.chess_platform.chat_service.dto.FriendRequestUpdateDto;
import net.chess_platform.chat_service.model.FriendRequest;
import net.chess_platform.chat_service.service.FriendService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {

    private FriendService friendService;

    public FriendRequestController(FriendService friendRequestService) {
        this.friendService = friendRequestService;
    }

    @GetMapping
    public List<FriendRequestDto> getAll(CurrentUser user) {
        return friendService.findAllRequests(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void create(@RequestBody FriendRequestCreateDto dto, CurrentUser user) {
        friendService.createRequest(dto.receiverId(), user);
    }

    @PatchMapping("/{friendRequestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable UUID friendRequestId, @RequestBody FriendRequestUpdateDto dto,
            CurrentUser user) {
        var update = new FriendRequest.Update();
        update.setStatus(dto.status());

        friendService.updateRequest(friendRequestId, update, user);
    }
}
