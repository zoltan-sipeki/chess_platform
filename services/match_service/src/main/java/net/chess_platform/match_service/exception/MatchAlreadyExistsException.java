package net.chess_platform.match_service.exception;

public class MatchAlreadyExistsException extends RuntimeException {

    public MatchAlreadyExistsException(String message) {
        super(message);
    }

    public MatchAlreadyExistsException() {
        super();
    }
}
