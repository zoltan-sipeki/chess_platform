package net.chess_platform.user_service.exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException() {
        super();
    }
}
