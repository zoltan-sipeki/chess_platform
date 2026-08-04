package net.chess_platform.chat_service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.chat_service.dto.NotificationDto;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.common.domain_events.broker.chat.NotificationEvent;
import net.chess_platform.common.domain_events.broker.chat.UserData;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2026-06-13T15:49:00+0200", comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)")
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationEvent.Payload toEventPayload(Notification notification) {
        if (notification == null) {
            return null;
        }

        long seq = 0L;
        UUID id = null;
        String type = null;
        UserData sender = null;
        UUID friendRequest = null;
        Instant createdAt = null;

        seq = notification.getSequenceNumber();
        id = notification.getId();
        if (notification.getType() != null) {
            type = notification.getType().name();
        }
        sender = userToUser(notification.getSender());
        friendRequest = notification.getFriendRequest();
        createdAt = notification.getCreatedAt();

        NotificationEvent.Payload payload = new NotificationEvent.Payload(id, seq, type, sender, friendRequest,
                createdAt);

        return payload;
    }

    @Override
    public List<NotificationDto> toDtoList(List<Notification> list) {
        if (list == null) {
            return null;
        }

        List<NotificationDto> list1 = new ArrayList<NotificationDto>(list.size());
        for (Notification notification : list) {
            list1.add(toDto(notification));
        }

        return list1;
    }

    @Override
    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        long seq = 0L;
        UUID id = null;
        String type = null;
        UserDto sender = null;
        UUID friendRequest = null;
        Instant createdAt = null;

        seq = notification.getSequenceNumber();
        id = notification.getId();
        if (notification.getType() != null) {
            type = notification.getType().name();
        }
        sender = userToUserDto(notification.getSender());
        friendRequest = notification.getFriendRequest();
        createdAt = notification.getCreatedAt();

        NotificationDto notificationDto = new NotificationDto(id, type, seq, sender, friendRequest, createdAt);

        return notificationDto;
    }

    protected UserData userToUser(net.chess_platform.chat_service.model.User user) {
        if (user == null) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();

        String presence = null;
        String activity = null;

        UserData user1 = new UserData(id, displayName, avatar, presence, activity);

        return user1;
    }

    protected UserDto userToUserDto(net.chess_platform.chat_service.model.User user) {
        if (user == null) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();

        UserDto userDto = new UserDto(id, displayName, avatar, null, null);

        return userDto;
    }
}
