package net.chess_platform.user_service.exception;

public class InvalidImageException extends RuntimeException {

    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException() {
        super();
    }
}
