package net.chess_platform.chat_service.dto;

import com.mongodb.lang.NonNull;

import net.chess_platform.chat_service.model.Privacy;

public record UpdatePrivacyDto(
		@NonNull Privacy.Restriction.Setting friends) {
}
