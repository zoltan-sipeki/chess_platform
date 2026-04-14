package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.user.UserDto;

public class UserCreatedEvent extends DomainEvent<UserDto> {

    public UserCreatedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_CREATED);
    }

    public UserCreatedEvent(UUID userId, String username, String displayName, String email, String avatar) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_CREATED,
                new UserDto(userId, username, displayName, email, avatar));
    }

}
