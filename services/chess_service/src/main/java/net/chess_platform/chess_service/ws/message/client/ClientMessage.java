package net.chess_platform.chess_service.ws.message.client;

import java.util.Map;

public class ClientMessage {

    public enum Type {
        CONNECT,
        AUTHENTICATE,
        RECONNECT,
        MOVE,
        PROMOTION,
        RESIGN
    }

    public static final Map<Type, Class<?>> PAYLOAD_MAPPING = Map.of(
            Type.AUTHENTICATE, AuthenticatePayload.class,
            Type.RECONNECT, ReconnectPayload.class,
            Type.MOVE, MovePayload.class,
            Type.PROMOTION, PromotionPayload.class,
            Type.RESIGN, ResignPayload.class,
            Type.CONNECT, ConnectPayload.class);

    private Type type;

    private Object payload;

    public ClientMessage(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }

}
