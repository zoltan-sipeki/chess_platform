package net.chess_platform.common.domain_events.broker.message.queue.backend;

public record ErrorReply(ErrorCause cause, String message) {

    public enum ErrorCause {
        MATCHMAKING_ERROR,
        SERVICE_UNAVAILABLE,
        INTERNAL_SERVER_ERROR
    }
}
