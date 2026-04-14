package net.chess_platform.chat_service.exception;

public class InvalidFriendRequestException extends RuntimeException {

    public InvalidFriendRequestException(String message) {
        super(message);
    }

    public InvalidFriendRequestException() {
        super();
    }
}
