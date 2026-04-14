package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.dto.chat.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T20:57:42+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

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
    public List<UserDto> toDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( users.size() );
        for ( User user : users ) {
            list.add( toDto( user ) );
        }

        return list;
    }

    @Override
    public List<UserDto> toDtoListFromChannelMember(List<ChannelMember> members) {
        if ( members == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( members.size() );
        for ( ChannelMember channelMember : members ) {
            list.add( channelMemberToUserDto( channelMember ) );
        }

        return list;
    }

    @Override
    public UserDto toDto(Friend friend) {
        if ( friend == null ) {
            return null;
        }

        UUID id = friend.getFriend().get(0).getId();
        String displayName = friend.getFriend().get(0).getDisplayName();
        String avatar = friend.getFriend().get(0).getAvatar();
        String presence = null;

        UserDto userDto = new UserDto( id, displayName, avatar, presence );

        return userDto;
    }

    @Override
    public List<UserDto> toDtoListFromFriend(List<Friend> friends) {
        if ( friends == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( friends.size() );
        for ( Friend friend : friends ) {
            list.add( toDto( friend ) );
        }

        return list;
    }

    protected UserDto channelMemberToUserDto(ChannelMember channelMember) {
        if ( channelMember == null ) {
            return null;
        }

        UUID id = null;

        id = channelMember.getId();

        String displayName = null;
        String avatar = null;
        String presence = null;

        UserDto userDto = new UserDto( id, displayName, avatar, presence );

        return userDto;
    }
}
