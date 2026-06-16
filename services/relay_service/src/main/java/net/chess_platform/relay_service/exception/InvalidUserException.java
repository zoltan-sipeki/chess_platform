package net.chess_platform.relay_service.exception;

public class InvalidUserException extends RuntimeException {

    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException() {
        super();
    }
}
