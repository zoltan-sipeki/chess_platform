package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.dto.chat.ChannelDto;
import net.chess_platform.common.dto.chat.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T11:55:27+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

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
        members = userListToUserDtoList( channel.getMembers() );

        ChannelDto channelDto = new ChannelDto( id, name, type, members );

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

    protected UserDto userToUserDto(User user) {
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

    protected List<UserDto> userListToUserDtoList(List<User> list) {
        if ( list == null ) {
            return null;
        }

        List<UserDto> list1 = new ArrayList<UserDto>( list.size() );
        for ( User user : list ) {
            list1.add( userToUserDto( user ) );
        }

        return list1;
    }
}
