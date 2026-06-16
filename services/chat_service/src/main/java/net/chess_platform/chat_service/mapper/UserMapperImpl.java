package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.domain_events.broker.user.UserEventData;
import org.springframework.stereotype.Component;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2026-06-15T19:44:23+0200", comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)")
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User.Update toUpdate(UserEventData userDto) {
        if (userDto == null) {
            return null;
        }

        User.Update update = new User.Update();

        update.setDisplayName(userDto.displayName());
        update.setAvatar(userDto.avatar());

        return update;
    }

    @Override
    public UserDto toDto(User user) {
        if (user == null) {
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
        if (user.getPresence() != null) {
            presence = user.getPresence().name();
        }
        if (user.getActivity() != null) {
            activity = user.getActivity().name();
        }

        UserDto userDto = new UserDto(id, displayName, avatar, presence, activity);

        return userDto;
    }

    @Override
    public List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>(users.size());
        for (User user : users) {
            list.add(toDto(user));
        }

        return list;
    }

    @Override
    public List<UserDto> toDtoListFromChannelMember(List<ChannelMember> members) {
        if (members == null) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>(members.size());
        for (ChannelMember channelMember : members) {
            list.add(channelMemberToUserDto(channelMember));
        }

        return list;
    }

    @Override
    public UserDto toDto(Friend friend) {
        if (friend == null) {
            return null;
        }

        UUID id = friend.getFriend().getId();
        String displayName = friend.getFriend().getDisplayName();
        String avatar = friend.getFriend().getAvatar();
        String presence = null;
        var p = friend.getFriend().getPresence();
        if (p != null) {
            presence = p.toString();
        }
        String activity = null;
        var a = friend.getFriend().getActivity();
        if (a != null) {
            activity = a.toString();
        }

        UserDto userDto = new UserDto(id, displayName, avatar, presence, activity);

        return userDto;
    }

    @Override
    public UserDto toDtoWithoutPresence(Friend friend) {
        if (friend == null) {
            return null;
        }

        UUID id = friend.getFriend().getId();
        String displayName = friend.getFriend().getDisplayName();
        String avatar = friend.getFriend().getAvatar();
        String presence = null;
        String activity = null;

        UserDto userDto = new UserDto(id, displayName, avatar, presence, activity);

        return userDto;
    }

    @Override
    public List<UserDto> toDtoListFromFriend(List<Friend> friends) {
        if (friends == null) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>(friends.size());
        for (Friend friend : friends) {
            list.add(toDto(friend));
        }

        return list;
    }

    @Override
    public List<UserDto> toDtoListFromFriendWithoutPresence(List<Friend> friends) {
        if (friends == null) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>(friends.size());
        for (Friend friend : friends) {
            list.add(toDtoWithoutPresence(friend));
        }

        return list;
    }

    protected UserDto channelMemberToUserDto(ChannelMember channelMember) {
        if (channelMember == null) {
            return null;
        }

        UUID id = null;

        id = channelMember.getId();

        String displayName = null;
        String avatar = null;
        String presence = null;
        String activity = null;

        UserDto userDto = new UserDto(id, displayName, avatar, presence, activity);

        return userDto;
    }
}
