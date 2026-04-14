package net.chess_platform.user_service.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.common.dto.user.UserDto;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.model.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-26T23:23:55+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 23 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toModel(UserDto dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( dto.username() );
        user.setDisplayName( dto.displayName() );
        user.setAvatar( dto.avatar() );
        user.setEmail( dto.email() );

        return user;
    }

    @Override
    public User toModel(KeycloakUserVerifiedMessage message) {
        if ( message == null ) {
            return null;
        }

        User user = new User();

        if ( message.id() != null ) {
            user.setId( UUID.fromString( message.id() ) );
        }
        user.setUsername( message.username() );
        user.setDisplayName( message.displayName() );
        user.setAvatar( message.avatar() );
        user.setEmail( message.email() );

        return user;
    }

    @Override
    public User toModel(KeycloakUserRepresentation kcUser) {
        if ( kcUser == null ) {
            return null;
        }

        User user = new User();

        if ( kcUser.getId() != null ) {
            user.setId( UUID.fromString( kcUser.getId() ) );
        }
        user.setUsername( kcUser.getUsername() );
        user.setDisplayName( kcUser.getDisplayName() );
        user.setAvatar( kcUser.getAvatar() );
        user.setEmail( kcUser.getEmail() );

        return user;
    }

    @Override
    public KeycloakUserRepresentation toKeycloakUserRepresentation(User user) {
        if ( user == null ) {
            return null;
        }

        KeycloakUserRepresentation keycloakUserRepresentation = new KeycloakUserRepresentation();

        if ( user.getId() != null ) {
            keycloakUserRepresentation.setId( user.getId().toString() );
        }
        keycloakUserRepresentation.setUsername( user.getUsername() );
        keycloakUserRepresentation.setEmail( user.getEmail() );
        keycloakUserRepresentation.setDisplayName( user.getDisplayName() );
        keycloakUserRepresentation.setAvatar( user.getAvatar() );

        return keycloakUserRepresentation;
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        String username = null;
        String displayName = null;
        String email = null;
        String avatar = null;

        username = user.getUsername();
        displayName = user.getDisplayName();
        email = user.getEmail();
        avatar = user.getAvatar();

        UUID userId = null;

        UserDto userDto = new UserDto( userId, username, displayName, email, avatar );

        return userDto;
    }

    @Override
    public KeycloakUserRepresentation toKeycloakUserRepresentation(KeycloakUserVerifiedMessage dto) {
        if ( dto == null ) {
            return null;
        }

        KeycloakUserRepresentation keycloakUserRepresentation = new KeycloakUserRepresentation();

        keycloakUserRepresentation.setId( dto.id() );
        keycloakUserRepresentation.setUsername( dto.username() );
        keycloakUserRepresentation.setEmail( dto.email() );
        keycloakUserRepresentation.setDisplayName( dto.displayName() );
        keycloakUserRepresentation.setAvatar( dto.avatar() );

        return keycloakUserRepresentation;
    }
}
