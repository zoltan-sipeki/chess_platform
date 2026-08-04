package net.chess_platform.chat_service.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.dto.MessageDto;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.UserData;

import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-31T13:16:54+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 23 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public UserData toEventUser(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;
        String presence = null;
        String activity = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();
        if ( user.getPresence() != null ) {
            presence = user.getPresence().name();
        }
        if ( user.getActivity() != null ) {
            activity = user.getActivity().name();
        }

        UserData user1 = new UserData( id, displayName, avatar, presence, activity );

        return user1;
    }

    @Override
    public MessageCreatedEvent.Payload toEventPayload(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID id = null;
        UUID channelId = null;
        long sequenceNumber = 0L;
        String content = null;
        Instant createdAt = null;
        Instant lastEditedAt = null;

        id = message.getId();
        channelId = message.getChannelId();
        sequenceNumber = message.getSequenceNumber();
        content = message.getContent();
        createdAt = message.getCreatedAt();
        lastEditedAt = message.getLastEditedAt();

        UserData sender = toEventUser(message.getSender());

        MessageCreatedEvent.Payload payload = new MessageCreatedEvent.Payload( id, channelId, sender, sequenceNumber, content, createdAt, lastEditedAt );

        return payload;
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;
        String presence = null;
        String activity = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();
        if ( user.getPresence() != null ) {
            presence = user.getPresence().name();
        }
        if ( user.getActivity() != null ) {
            activity = user.getActivity().name();
        }

        UserDto userDto = new UserDto( id, displayName, avatar, presence, activity );

        return userDto;
    }

    @Override
    public MessageDto toDto(Message message) {
        if ( message == null ) {
            return null;
        }

        UUID id = null;
        UUID channelId = null;
        UserDto sender = null;
        long sequenceNumber = 0L;
        String content = null;
        Instant createdAt = null;
        Instant lastEditedAt = null;

        id = message.getId();
        channelId = message.getChannelId();
        sender = toDto( message.getSender() );
        sequenceNumber = message.getSequenceNumber();
        content = message.getContent();
        createdAt = message.getCreatedAt();
        lastEditedAt = message.getLastEditedAt();

        MessageDto messageDto = new MessageDto( id, channelId, sender, sequenceNumber, content, createdAt, lastEditedAt );

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
