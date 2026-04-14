package net.chess_platform.relay_service.exception;

public class InvalidMessageException extends RuntimeException{

    public InvalidMessageException(String message) {
        super(message);
    }

    public InvalidMessageException() {
        super();
    }
}
