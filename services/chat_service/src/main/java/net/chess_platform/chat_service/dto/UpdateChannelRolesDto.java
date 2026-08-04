package net.chess_platform.chat_service.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateChannelRolesDto(
		@NotNull UUID memberId,
		@NotEmpty List<String> roles) {

}
