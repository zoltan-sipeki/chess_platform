package net.chess_platform.match_service.exception;

public class UserAlreadyInMatchException extends RuntimeException {
    public UserAlreadyInMatchException(String message) {
        super(message);
    }
}
