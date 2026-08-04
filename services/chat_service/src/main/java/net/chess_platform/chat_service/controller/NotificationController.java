package net.chess_platform.chat_service.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.NotificationListDto;
import net.chess_platform.chat_service.dto.UpdateNotificationReadStateDto;
import net.chess_platform.chat_service.service.NotificationService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public NotificationListDto getAll(CurrentUser user, Optional<Long> before,
            @RequestParam(defaultValue = "10") long limit) {
        return notificationService.findAll(user, before.orElse(null), limit);
    }

    @PutMapping("/read-state")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putAll(@RequestBody UpdateNotificationReadStateDto dto, CurrentUser user) {
        notificationService.updateReadState(dto.lastReadSequenceNumber(), user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable UUID id, CurrentUser user) {
        notificationService.delete(id, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(CurrentUser user) {
        notificationService.deleteAll(user);
    }
}
