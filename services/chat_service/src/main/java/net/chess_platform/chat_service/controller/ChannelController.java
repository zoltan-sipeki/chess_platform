package net.chess_platform.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.chess_platform.chat_service.dto.CreateChannelRequest;
import net.chess_platform.chat_service.dto.CreateMessageRequest;
import net.chess_platform.chat_service.dto.UpdateChannelNameRequest;
import net.chess_platform.chat_service.dto.UpdateChannelRolesRequest;
import net.chess_platform.chat_service.dto.UpdateChatMessageRequest;
import net.chess_platform.chat_service.dto.UpdateLastReadMessageRequest;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.service.ChannelService;
import net.chess_platform.chat_service.service.MessageService;
import net.chess_platform.common.dto.chat.ChannelDto;
import net.chess_platform.common.dto.chat.MessageDto;
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
        return channelService.getChannels(currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDto createChannel(@RequestBody @Valid CreateChannelRequest request,
            CurrentUser currentUser) {
        var type = !StringUtils.hasText(request.type()) ? Channel.Type.DM : Channel.Type.valueOf(request.type());
        return channelService.createChannel(type, request.recipients(), currentUser);
    }

    @PatchMapping("/{channelId}")
    public ChannelDto updateChannelName(@PathVariable UUID channelId, @RequestBody UpdateChannelNameRequest request,
            CurrentUser currentUser) {
        return channelService.updateName(channelId, request.name(), currentUser);
    }

    @DeleteMapping("/{channelId}/me/history")
    public void clearChannelHistory(@PathVariable UUID channelId, CurrentUser currentUser) {
        channelService.clearChannelHistory(channelId, currentUser);
    }

    @GetMapping("/{channelId}/messages")
    public List<MessageDto> getMessagesByChannelId(@PathVariable UUID channelId,
            @RequestParam(defaultValue = "50") int limit,
            CurrentUser currentUser) {
        return messageService.findAll(channelId, limit, currentUser);
    }

    @PostMapping("/{channelId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto createMessageInChannel(@PathVariable UUID channelId,
            @RequestBody CreateMessageRequest dto,
            CurrentUser currentUser) {
        return messageService.create(channelId, dto.content(), currentUser);
    }

    @PatchMapping("/{channelId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto updateMessage(@PathVariable UUID channelId, @PathVariable long messageId,
            @RequestBody UpdateChatMessageRequest dto, CurrentUser currentUser) {
        return messageService.updateContent(channelId, messageId, dto.content(),
                currentUser);
    }

    @DeleteMapping("/{channelId}/messages/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable UUID channelId, @PathVariable long messageId,
            CurrentUser currentUser) {
        messageService.delete(channelId, messageId, currentUser);
    }

    @PostMapping("/{channelId}/typing")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void broadcastTyping(@PathVariable UUID channelId) {
    }

    @PatchMapping("/{channelId}/members")
    public ChannelDto addChannelMembers(@PathVariable UUID channelId, @RequestBody List<UUID> members,
            CurrentUser currentUser) {
        return channelService.addChannelMembers(channelId, members, currentUser);
    }

    @DeleteMapping("/{channelId}/members/{userId}")
    public ChannelDto kickMember(@PathVariable UUID channelId, @PathVariable UUID userId,
            CurrentUser currentUser) {
        return channelService.kickMember(channelId, userId, currentUser);
    }

    @PatchMapping("/{channelId}/members/{userId}/roles")
    public void updateChannelRolesForMember(@PathVariable UUID channelId, @PathVariable UUID userId,
            @RequestBody UpdateChannelRolesRequest dto) {

    }

    @DeleteMapping("/{channelId}/members/me")
    public void leaveChannel(@PathVariable UUID channelId, CurrentUser currentUser) {
        channelService.leaveChannel(channelId, currentUser);
    }

    @PatchMapping("/{channelId}/me/unread")
    public void updateLastReadMessage(@PathVariable UUID channelId,
            @RequestBody UpdateLastReadMessageRequest request, CurrentUser currentUser) {
        channelService.updateLastReadMessage(channelId, request.messageId(), currentUser);
    }

}
