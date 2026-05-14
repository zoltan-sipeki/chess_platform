package net.chess_platform.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import net.chess_platform.common.dto.user.UserDto;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.dto.ProfileUserDto;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.model.User;

// @Mapper(componentModel = "spring")
public interface UserMapper {

    User toModel(UserDto dto);

    User toModel(KeycloakUserVerifiedMessage message);

    User toModel(KeycloakUserRepresentation kcUser);

    KeycloakUserRepresentation toKeycloakUserRepresentation(User user);

    UserDto toDto(User user);

    ProfileUserDto toProfileUserDto(User user);

    KeycloakUserRepresentation toKeycloakUserRepresentation(KeycloakUserVerifiedMessage dto);

    List<ProfileUserDto> toClientUserDtoList(List<User> users);
}
