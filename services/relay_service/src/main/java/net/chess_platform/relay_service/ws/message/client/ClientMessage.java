package net.chess_platform.relay_service.ws.message.client;

import java.util.Map;

public class ClientMessage {

    public enum Type {
        CONNECT,
        UPDATE_DEFAULT_STATUS
    }

    public static final Map<Type, Class<?>> PAYLOAD_MAPPING = Map.of(
            Type.CONNECT, ConnectPayload.class,
            Type.UPDATE_DEFAULT_STATUS, UpdatePreferredStatusPayload.class);

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
