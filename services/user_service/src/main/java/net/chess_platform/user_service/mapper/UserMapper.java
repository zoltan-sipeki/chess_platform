package net.chess_platform.user_service.mapper;

import java.util.List;

import net.chess_platform.keycloak.event.KeycloakUser;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.dto.UserDto;
import net.chess_platform.user_service.model.User;

// @Mapper(componentModel = "spring")
public interface UserMapper {

    User.Update toUpdate(KeycloakUser kcu);

    User.Update toUpdate(KeycloakUserRepresentation kcr);

    User toModel(KeycloakUser kcu);

    User toModel(KeycloakUserRepresentation kcr);

    KeycloakUserRepresentation toKeycloakUserRepresentation(KeycloakUser user);

    KeycloakUserRepresentation toKeycloakUserRepresentation(User user);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
