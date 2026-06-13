package net.chess_platform.common.domain_events.broker.user;

import java.util.UUID;

public record UserEventData(
        UUID id,

        String username,

        String displayName,

        String avatar,

        String email) {

}
