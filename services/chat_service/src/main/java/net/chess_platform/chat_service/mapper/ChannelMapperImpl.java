package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.dto.ChannelDto;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-31T15:34:42+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 23 (Oracle Corporation)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ChannelDto toDto(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String type = null;
        List<UserDto> members = null;

        id = channel.getId();
        name = channel.getName();
        if ( channel.getType() != null ) {
            type = channel.getType().name();
        }
        members = userSetToUserDtoList( channel.getMembers() );

        int unreadCount = 0;

        ChannelDto channelDto = new ChannelDto( id, name, type, unreadCount, members );

        return channelDto;
    }

    @Override
    public List<ChannelDto> toDtoList(List<Channel> channels) {
        if ( channels == null ) {
            return null;
        }

        List<ChannelDto> list = new ArrayList<ChannelDto>( channels.size() );
        for ( Channel channel : channels ) {
            list.add( toDto( channel ) );
        }

        return list;
    }

    @Override
    public GroupChannelCreatedEvent.Payload toEventPayload(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String type = null;
        List<UserData> members = null;

        id = channel.getId();
        name = channel.getName();
        if ( channel.getType() != null ) {
            type = channel.getType().name();
        }
        members = userSetToUserDataList( channel.getMembers() );

        GroupChannelCreatedEvent.Payload payload = new GroupChannelCreatedEvent.Payload( id, name, type, members );

        return payload;
    }

    protected List<UserDto> userSetToUserDtoList(Set<User> set) {
        if ( set == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( set.size() );
        for ( User user : set ) {
            list.add( userMapper.toDto( user ) );
        }

        return list;
    }

    protected UserData userToUserData(User user) {
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

        UserData userData = new UserData( id, displayName, avatar, presence, activity );

        return userData;
    }

    protected List<UserData> userSetToUserDataList(Set<User> set) {
        if ( set == null ) {
            return null;
        }

        List<UserData> list = new ArrayList<UserData>( set.size() );
        for ( User user : set ) {
            list.add( userToUserData( user ) );
        }

        return list;
    }
}
