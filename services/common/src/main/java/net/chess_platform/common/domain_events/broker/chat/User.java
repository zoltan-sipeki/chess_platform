package net.chess_platform.common.domain_events.broker.chat;

import java.util.UUID;

public record User(UUID id, String displayName, String avatar, String presence, String activity) {

}
