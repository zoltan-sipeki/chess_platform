package net.chess_platform.user_service.mapper;

import net.chess_platform.common.dto.user.UserDto;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.model.User;

public interface UserMapper {

    public User toModel(UserDto dto);

    public User toModel(KeycloakUserVerifiedMessage message);

    public User toModel(KeycloakUserRepresentation kcUser);

    public KeycloakUserRepresentation toKeycloakUserRepresentation(User user);

    public UserDto toDto(User user);

    public KeycloakUserRepresentation toKeycloakUserRepresentation(KeycloakUserVerifiedMessage dto);
}
