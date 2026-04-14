package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.Mapping;

import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.dto.chat.UserDto;

// @Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "avatar", source = "user.avatar")
    List<UserDto> toDtoListFromChannelMember(List<ChannelMember> members);

    @Mapping(target = "id", expression = "java(friend.getFriend().get(0).getId())")
    @Mapping(target = "displayName", expression = "java(friend.getFriend().get(0).getDisplayName())")
    @Mapping(target = "avatar", expression = "java(friend.getFriend().get(0).getAvatar())")
    UserDto toDto(Friend friend);

    List<UserDto> toDtoListFromFriend(List<Friend> friends);
}
