package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import net.chess_platform.chat_service.dto.UserDto;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.model.User;
import net.chess_platform.common.domain_events.broker.user.UserEventData;

// @Mapper(componentModel = "spring")
public interface UserMapper {

    User.Update toUpdate(UserEventData userDto);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "avatar", source = "user.avatar")
    List<UserDto> toDtoListFromChannelMember(List<ChannelMember> members);

    @Mapping(target = "id", expression = "java(friend.getFriend().getId())")
    @Mapping(target = "displayName", expression = "java(friend.getFriend().getDisplayName())")
    @Mapping(target = "avatar", expression = "java(friend.getFriend().getAvatar())")
    @Mapping(target = "presence", expression = "java(friend.getFriend().getPresence().toString())")
    @Mapping(target = "activity", expression = "java(friend.getFriend().getActivity().toString())")
    @Named("WithPresence")
    UserDto toDto(Friend friend);

    @Mapping(target = "id", expression = "java(friend.getFriend().getId())")
    @Mapping(target = "displayName", expression = "java(friend.getFriend().getDisplayName())")
    @Mapping(target = "avatar", expression = "java(friend.getFriend().getAvatar())")
    @Mapping(target = "presence", ignore = true)
    @Mapping(target = "activity", ignore = true)
    @Named("WithoutPresence")
    UserDto toDtoWithoutPresence(Friend friend);

    @IterableMapping(qualifiedByName = "WithPresence")
    List<UserDto> toDtoListFromFriend(List<Friend> friends);
    
    @IterableMapping(qualifiedByName = "WithoutPresence")
    List<UserDto> toDtoListFromFriendWithoutPresence(List<Friend> friends);
}
