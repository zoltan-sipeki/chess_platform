package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.user.UserDto;

public class UserDeletedEvent extends DomainEvent<UserDto> {

    public UserDeletedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_DELETED);
    }

    public UserDeletedEvent(UUID userId, String username, String displayName, String email, String avatar) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_DELETED,
                new UserDto(userId, username, displayName, email, avatar));
    }

}
