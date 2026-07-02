package net.chess_platform.chess_service.coordinator.message;

public interface IRoutableMessage {

    long getRoutingKey();
}
