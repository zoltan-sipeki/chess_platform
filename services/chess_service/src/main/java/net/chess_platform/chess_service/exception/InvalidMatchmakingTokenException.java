package net.chess_platform.chess_service.exception;

public class InvalidMatchmakingTokenException extends RuntimeException {

    public InvalidMatchmakingTokenException(String message) {
        super(message);
    }

    public InvalidMatchmakingTokenException() {
        super();
    }
}
