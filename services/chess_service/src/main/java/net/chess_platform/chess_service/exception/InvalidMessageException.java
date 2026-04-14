package net.chess_platform.chess_service.exception;

public class InvalidMessageException extends RuntimeException {

    public InvalidMessageException() {
    }

    public InvalidMessageException(String message) {
        super(message);
    }

}
