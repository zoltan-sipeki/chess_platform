package net.chess_platform.chat_service.dto;

import java.util.List;
import java.util.UUID;

public record RelationShipSearchRequest(
		List<UUID> ids) {

}
