package net.chess_platform.relay_service.dto;

import net.chess_platform.relay_service.model.RelayUser.Presence;

public record PreferredPresenceUpdateDto(
    Presence presence
) {

}
