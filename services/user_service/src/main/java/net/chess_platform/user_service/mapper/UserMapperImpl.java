package net.chess_platform.user_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.common.dto.user.UserDto;
import net.chess_platform.keycloak.KeycloakUserVerifiedMessage;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.dto.ProfileUserDto;
import net.chess_platform.user_service.model.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-14T10:06:32+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
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
    public ProfileUserDto toProfileUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();

        ProfileUserDto profileUserDto = new ProfileUserDto( id, displayName, avatar );

        return profileUserDto;
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

    @Override
    public List<ProfileUserDto> toClientUserDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<ProfileUserDto> list = new ArrayList<ProfileUserDto>( users.size() );
        for ( User user : users ) {
            list.add( toProfileUserDto( user ) );
        }

        return list;
    }
}
