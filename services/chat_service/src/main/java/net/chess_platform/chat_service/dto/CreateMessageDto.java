package net.chess_platform.chat_service.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateMessageDto(
		@NotEmpty String content) {

}
