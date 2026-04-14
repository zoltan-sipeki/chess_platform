package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.dto.user.UserDto;

public class UserUpdatedEvent extends DomainEvent<UserDto> {

    public UserUpdatedEvent() {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_UPDATED);
    }

    public UserUpdatedEvent(UUID userId, String username, String displayName, String email, String avatar) {
        super(DomainEvent.Category.USER, DomainEvent.Type.USER_UPDATED,
                new UserDto(userId, username, displayName, email, avatar));
    }

}
