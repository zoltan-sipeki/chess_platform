package net.chess_platform.chat_service.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.common.dto.chat.NotificationDto;
import net.chess_platform.common.dto.chat.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T20:57:42+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        UUID id = null;
        String type = null;
        UserDto sender = null;
        boolean read = false;

        id = notification.getId();
        if ( notification.getType() != null ) {
            type = notification.getType().name();
        }
        sender = userMapper.toDto( notification.getSender() );
        read = notification.isRead();

        NotificationDto notificationDto = new NotificationDto( id, type, sender, read );

        return notificationDto;
    }
}
