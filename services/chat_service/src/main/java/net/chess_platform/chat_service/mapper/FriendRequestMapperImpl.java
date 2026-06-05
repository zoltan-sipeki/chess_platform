package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.dto.FriendRequestDto;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.FriendRequest;
import net.chess_platform.chat_service.model.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-05T15:42:19+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class FriendRequestMapperImpl implements FriendRequestMapper {

    @Override
    public FriendRequestDto toDto(FriendRequest friendRequest) {
        if ( friendRequest == null ) {
            return null;
        }

        UUID id = null;
        UserDto sender = null;

        id = friendRequest.getId();
        sender = userToUserDto( friendRequest.getSender() );

        FriendRequestDto friendRequestDto = new FriendRequestDto( id, sender );

        return friendRequestDto;
    }

    @Override
    public List<FriendRequestDto> toDtoList(List<FriendRequest> friendRequest) {
        if ( friendRequest == null ) {
            return null;
        }

        List<FriendRequestDto> list = new ArrayList<FriendRequestDto>( friendRequest.size() );
        for ( FriendRequest friendRequest1 : friendRequest ) {
            list.add( toDto( friendRequest1 ) );
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
        String activity = null;

        UserDto userDto = new UserDto( id, displayName, avatar, presence, activity );

        return userDto;
    }
}
