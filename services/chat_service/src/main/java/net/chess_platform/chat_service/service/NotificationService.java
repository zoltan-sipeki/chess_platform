package net.chess_platform.chat_service.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.dto.NotificationListDto;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.NotificationMapper;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.NotificationRepository;
import net.chess_platform.common.security.CurrentUser;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final PermissionService permissionService;

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
            PermissionService permissionService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.permissionService = permissionService;
        this.notificationMapper = notificationMapper;
    }

    public NotificationListDto findAll(CurrentUser user, Long before, long limit) {
        var auth = permissionService.authorize(Action.NOTIFICATION_QUERY, user, null);
        var result = notificationRepository.findAll(auth, before, limit);

        Long last = result.isEmpty() ? null : result.getLast().getSequenceNumber();
        long unread = notificationRepository.countUnread(auth);
        long lastReadSeq = notificationRepository.getLastReadSequenceNumber(auth);

        return new NotificationListDto(unread, lastReadSeq,
                notificationMapper.toDtoList(result), last);
    }

    public void delete(UUID notificationId, CurrentUser user) {
        var auth = permissionService.authorize(Action.NOTIFICATION_DELETE, user,
                Map.of("notificationId", notificationId));

        long deletedCount = notificationRepository.deleteOne(auth);

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void updateAll(Notification.Update update, CurrentUser user) {
        var auth = permissionService.authorize(Action.NOTIFICATION_UPDATE, user, null);
        notificationRepository.updateAll(update, auth);
    }

    public void deleteAll(CurrentUser user) {
        var auth = permissionService.authorize(Action.NOTIFICATION_DELETE, user, null);
        notificationRepository.deleteAll(auth);
    }
}
