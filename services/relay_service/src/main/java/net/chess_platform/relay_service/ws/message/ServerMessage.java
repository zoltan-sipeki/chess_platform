package net.chess_platform.relay_service.ws.message;

public class ServerMessage {

    public enum Type {
        ERROR,
        CONNECTED,
        EVENT
    }

    private Type type;

    private Object payload;

    public ServerMessage(Type type) {
        this.type = type;
    }

    public ServerMessage(Type type, Object payload) {
        this(type);
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
