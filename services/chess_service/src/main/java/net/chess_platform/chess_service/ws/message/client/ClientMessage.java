package net.chess_platform.chess_service.ws.message.client;

import java.util.Map;

public class ClientMessage {

    public enum Type {
        JOIN_MATCH,
        AUTHENTICATE,
        MOVE,
        PROMOTION,
        RESIGN
    }

    public static final Map<Type, Class<?>> PAYLOAD_MAPPING = Map.of(
            Type.AUTHENTICATE, AuthenticatePayload.class,
            Type.MOVE, MovePayload.class,
            Type.PROMOTION, PromotionPayload.class,
            Type.JOIN_MATCH, JoinMatchPayload.class,
            Type.RESIGN, ResignPayload.class);

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
