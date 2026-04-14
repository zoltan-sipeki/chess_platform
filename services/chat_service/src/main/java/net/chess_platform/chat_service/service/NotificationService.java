package net.chess_platform.chat_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.NotificationRepository;
import net.chess_platform.common.security.CurrentUser;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    private PermissionService permissionService;

    public NotificationService(NotificationRepository notificationRepository,
            PermissionService permissionService) {
        this.notificationRepository = notificationRepository;
        this.permissionService = permissionService;
    }

    public List<Notification> findAll(CurrentUser user) {
        var auth = permissionService.authorize(Action.NOTIFICATION_QUERY, user, null);
        return notificationRepository.findAll(auth);
    }

    public void delete(UUID notificationId, CurrentUser user) {
        var auth = permissionService.authorize(Action.NOTIFICATION_DELETE, user,
                Map.of("notificationId", notificationId));

        long deletedCount = notificationRepository.delete(auth);

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }
    }
}
