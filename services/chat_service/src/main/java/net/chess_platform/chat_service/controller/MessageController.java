package net.chess_platform.chat_service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import net.chess_platform.chat_service.dto.UpdateMessageContentDto;
import net.chess_platform.chat_service.service.MessageService;
import net.chess_platform.common.security.CurrentUser;

@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PatchMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateContent(@PathVariable UUID messageId, @RequestBody UpdateMessageContentDto dto,
            CurrentUser currentUser) {
        messageService.updateContent(messageId, dto.content(), currentUser);
    }

    @DeleteMapping("/{messageId}")
    public void delete(@PathVariable UUID messageId, CurrentUser currentUser) {
        messageService.delete(messageId, currentUser);
    }
}
