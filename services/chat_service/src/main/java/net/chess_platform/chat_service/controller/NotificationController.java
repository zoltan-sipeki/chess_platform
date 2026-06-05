package net.chess_platform.chat_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.NotificationListDto;
import net.chess_platform.chat_service.dto.NotificationUpdateDto;
import net.chess_platform.chat_service.model.Notification;
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
    public NotificationListDto getAll(CurrentUser user,
            Pageable pageable) {
        return notificationService.findAll(user, pageable);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchAll(@RequestBody NotificationUpdateDto dto, CurrentUser user) {
        var update = new Notification.Update();
        update.setLastReadSequenceNumber(dto.lastReadSequenceNumber());

        notificationService.updateAll(update, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable UUID notificationId, CurrentUser user) {
        notificationService.delete(notificationId, user);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll(CurrentUser user) {
        notificationService.deleteAll(user);
    }
}
