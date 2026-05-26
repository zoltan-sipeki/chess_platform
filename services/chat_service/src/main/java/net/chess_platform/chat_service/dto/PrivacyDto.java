package net.chess_platform.chat_service.dto;

import net.chess_platform.chat_service.model.Privacy;

public record PrivacyDto(
        Privacy.Restriction.Setting friends) {

}
