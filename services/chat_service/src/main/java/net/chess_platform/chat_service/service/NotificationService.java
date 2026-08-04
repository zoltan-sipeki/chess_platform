package net.chess_platform.chat_service.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.NotificationAuthorizationService;
import net.chess_platform.chat_service.dto.NotificationListDto;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.NotificationMapper;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.NotificationMetadata;
import net.chess_platform.chat_service.repository.NotificationMetadataRepository;
import net.chess_platform.chat_service.repository.NotificationRepository;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMetadataRepository notificationMetadataRepository;

    private final NotificationAuthorizationService authService;

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationRepository notificationRepository,
            NotificationMetadataRepository notificationMetadataRepository,
            NotificationAuthorizationService authService, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMetadataRepository = notificationMetadataRepository;
        this.authService = authService;
        this.notificationMapper = notificationMapper;
    }

    public NotificationListDto findAll(CurrentUser user, Long before, long limit) {
        var auth = authService.authorizeNotificationQuery(user);

        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

        var result = notificationRepository.findAll(fragment.getCriteria(), before, limit);

        if (result.isEmpty()) {
            return new NotificationListDto(0, 0, new ArrayList<>(), null);
        }

        var metadata = notificationMetadataRepository.findOne(user.id());

        long last = result.getLast().getSequenceNumber();
        long unread = notificationRepository.countUnread(fragment.getCriteria(), last);

        return new NotificationListDto(unread, metadata.getLastReadSequenceNumber(),
                notificationMapper.toDtoList(result), last);
    }

    public void delete(UUID notificationId, CurrentUser user) {
        var auth = authService.authorizeNotificationDelete(user, notificationId);

        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

        long deletedCount = notificationRepository.deleteOne(fragment.getCriteria());

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void updateReadState(long lastReadSequenceNumber, CurrentUser user) {
        var auth = authService.authorizeNotificationReadStateUpdate(user);
        MongoQueryFragment<NotificationMetadata> fragment = auth.getQueryFragment(NotificationMetadata.class);

        long modifiedCount = notificationMetadataRepository.updateLastReadSequenceNumber(fragment.getCriteria(),
                lastReadSequenceNumber);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void deleteAll(CurrentUser user) {
        var auth = authService.authorizeNotificationDelete(user);

        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

        long deletedCount = notificationRepository.deleteAll(fragment.getCriteria());

        if (deletedCount == 0) {
            throw new EntityNotFoundException();
        }
    }
}
