package net.chess_platform.match_service.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String msg) {
        super(msg);
    }

    public EntityNotFoundException() {
        super();
    }
}
