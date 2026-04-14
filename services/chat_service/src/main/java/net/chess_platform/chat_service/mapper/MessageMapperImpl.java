package net.chess_platform.chat_service.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.dto.chat.MessageDto;
import net.chess_platform.common.dto.chat.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T20:57:42+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();

        String presence = null;

        UserDto userDto = new UserDto( id, displayName, avatar, presence );

        return userDto;
    }

    @Override
    public MessageDto toDto(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID id = null;
        UUID channelId = null;
        long messageId = 0L;
        String content = null;
        OffsetDateTime createdAt = null;
        OffsetDateTime lastEditedAt = null;

        id = message.getId();
        channelId = message.getChannelId();
        messageId = message.getMessageId();
        content = message.getContent();
        createdAt = message.getCreatedAt();
        lastEditedAt = message.getLastEditedAt();

        UserDto sender = toDto(message.getSender());

        MessageDto messageDto = new MessageDto( id, channelId, sender, messageId, content, createdAt, lastEditedAt );

        return messageDto;
    }

    @Override
    public List<MessageDto> toDtoList(List<Message> messages) {
        if ( messages == null ) {
            return null;
        }

        List<MessageDto> list = new ArrayList<MessageDto>( messages.size() );
        for ( Message message : messages ) {
            list.add( toDto( message ) );
        }

        return list;
    }
}
