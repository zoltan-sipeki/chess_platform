package net.chess_platform.user_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.keycloak.event.KeycloakUser;
import net.chess_platform.user_service.dto.KeycloakUserRepresentation;
import net.chess_platform.user_service.dto.UserDto;
import net.chess_platform.user_service.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-28T13:21:28+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User.Update toUpdate(KeycloakUser kcu) {
        if ( kcu == null ) {
            return null;
        }

        User.Update update = new User.Update();

        update.setUsername( kcu.getUsername() );
        update.setEmail( kcu.getEmail() );
        if ( kcu.getId() != null ) {
            update.setId( UUID.fromString( kcu.getId() ) );
        }

        return update;
    }

    @Override
    public User.Update toUpdate(KeycloakUserRepresentation kcr) {
        if ( kcr == null ) {
            return null;
        }

        User.Update update = new User.Update();

        update.setUsername( kcr.getUsername() );
        update.setEmail( kcr.getEmail() );
        if ( kcr.getId() != null ) {
            update.setId( UUID.fromString( kcr.getId() ) );
        }

        return update;
    }

    @Override
    public User toModel(KeycloakUser kcu) {
        if ( kcu == null ) {
            return null;
        }

        User user = new User();

        if ( kcu.getId() != null ) {
            user.setId( UUID.fromString( kcu.getId() ) );
        }
        user.setUsername( kcu.getUsername() );
        user.setEmail( kcu.getEmail() );

        return user;
    }

    @Override
    public User toModel(KeycloakUserRepresentation kcr) {
        if ( kcr == null ) {
            return null;
        }

        User user = new User();

        if ( kcr.getId() != null ) {
            user.setId( UUID.fromString( kcr.getId() ) );
        }
        user.setUsername( kcr.getUsername() );
        user.setEmail( kcr.getEmail() );

        return user;
    }

    @Override
    public KeycloakUserRepresentation toKeycloakUserRepresentation(KeycloakUser user) {
        if ( user == null ) {
            return null;
        }

        KeycloakUserRepresentation keycloakUserRepresentation = new KeycloakUserRepresentation();

        keycloakUserRepresentation.setId( user.getId() );
        keycloakUserRepresentation.setUsername( user.getUsername() );
        keycloakUserRepresentation.setEmail( user.getEmail() );

        return keycloakUserRepresentation;
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

        return keycloakUserRepresentation;
    }

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String displayName = null;
        String avatar = null;

        id = user.getId();
        displayName = user.getDisplayName();
        avatar = user.getAvatar();

        UserDto userDto = new UserDto( id, displayName, avatar );

        return userDto;
    }

    @Override
    public List<UserDto> toDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( users.size() );
        for ( User user : users ) {
            list.add( toDto( user ) );
        }

        return list;
    }
}
