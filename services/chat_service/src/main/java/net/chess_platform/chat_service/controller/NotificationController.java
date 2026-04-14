package net.chess_platform.chat_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Notification> getAll(@AuthenticationPrincipal CurrentUser user,
            @RequestParam(defaultValue = "10") int limit) {
        return notificationService.findAll(user);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(@PathVariable UUID notificationId, CurrentUser user) {
        notificationService.delete(notificationId, user);
    }
}
