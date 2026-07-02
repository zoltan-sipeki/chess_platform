package net.chess_platform.chess_service.coordinator.dto;

import java.util.UUID;

public record PlayerDto(

		UUID id,

		String color,

		Integer mmrBefore,

		Integer mmrAfter,

		float score) {

}
