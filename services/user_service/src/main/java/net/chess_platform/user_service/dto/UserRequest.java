package net.chess_platform.user_service.dto;

public record UserRequest(
        String username,
        String displayName,
        String email,
        String password) {

}
