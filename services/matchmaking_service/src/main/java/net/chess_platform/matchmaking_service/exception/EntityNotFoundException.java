package net.chess_platform.matchmaking_service.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException() {
        super();
    }
}
