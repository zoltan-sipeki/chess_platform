package net.chess_platform.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.chess_platform.chat_service.dto.ChannelDto;
import net.chess_platform.chat_service.dto.CreateChannelDto;
import net.chess_platform.chat_service.dto.CreateMessageDto;
import net.chess_platform.chat_service.dto.MessageDto;
import net.chess_platform.chat_service.dto.UpdateChannelNameDto;
import net.chess_platform.chat_service.dto.UpdateChannelRolesDto;
import net.chess_platform.chat_service.dto.UpdateUnreadDto;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.service.ChannelService;
import net.chess_platform.chat_service.service.MessageService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private ChannelService channelService;

    private MessageService messageService;

    public ChannelController(ChannelService channelService, MessageService messageService) {
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @GetMapping
    public List<ChannelDto> getChannels(CurrentUser currentUser) {
        return channelService.findChannels(currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createChannel(@RequestBody @Valid CreateChannelDto dto,
            CurrentUser currentUser) {
        var type = dto == null ? Channel.Type.DM : dto.type();
        var members = dto.members();
        members.add(currentUser.id());

        return channelService.createChannel(type, members, currentUser);
    }

    @PatchMapping("/{channelId}")
    public void updateChannelName(@PathVariable UUID channelId, @RequestBody UpdateChannelNameDto dto,
            CurrentUser currentUser) {
        channelService.updateName(channelId, dto.name(), currentUser);
    }

    @GetMapping("/{channelId}/messages")
    public List<MessageDto> getMessagesByChannelId(@PathVariable UUID channelId,
            @RequestParam(defaultValue = "50") int limit,
            CurrentUser currentUser) {
        return messageService.findAll(channelId, limit, currentUser);
    }

    @PostMapping("/{channelId}/messages")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createMessage(@PathVariable UUID channelId,
            @RequestBody @Valid CreateMessageDto dto,
            CurrentUser currentUser) {
        messageService.create(channelId, dto.content(), currentUser);
    }

    @PostMapping("/{channelId}/typing")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void broadcastTyping(@PathVariable UUID channelId, CurrentUser user) {
        channelService.broadcastTyping(channelId, user);
    }

    @PatchMapping("/{channelId}/members")
    public void addChannelMembers(@PathVariable UUID channelId, @RequestBody List<UUID> members,
            CurrentUser currentUser) {
        channelService.addChannelMembers(channelId, members, currentUser);
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    public void kickMember(@PathVariable UUID channelId, @PathVariable UUID userId,
            CurrentUser currentUser) {
        channelService.kickMember(channelId, userId, currentUser);
    }

    @PatchMapping("/{channelId}/members/{userId}/roles")
    public void updateChannelRoles(@PathVariable UUID channelId, @PathVariable UUID userId,
            @RequestBody @Valid UpdateChannelRolesDto dto) {

    }

    @DeleteMapping("/{channelId}/members/me")
    public void leaveChannel(@PathVariable UUID channelId, CurrentUser currentUser) {
        channelService.leaveChannel(channelId, currentUser);
    }

    @DeleteMapping("/{channelId}/members/me/history")
    public void clearChannelHistory(@PathVariable UUID channelId, CurrentUser currentUser) {
        channelService.clearChannelHistory(channelId, currentUser);
    }

    @PutMapping("/{channelId}/members/me/unread")
    public void updateLastReadMessage(@PathVariable UUID channelId,
            @RequestBody UpdateUnreadDto request, CurrentUser currentUser) {
        channelService.updateLastReadMessage(channelId, request.sequenceNumber(), currentUser);
    }

}
