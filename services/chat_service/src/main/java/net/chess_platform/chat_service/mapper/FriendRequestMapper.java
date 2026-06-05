package net.chess_platform.chat_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import net.chess_platform.chat_service.dto.FriendRequestDto;
import net.chess_platform.chat_service.model.FriendRequest;

// @Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface FriendRequestMapper {

    FriendRequestDto toDto(FriendRequest friendRequest);

    List<FriendRequestDto> toDtoList(List<FriendRequest> friendRequest);
}
