package net.chess_platform.relay_service.ws.message.client;

public class ConnectPayload {

    private String accessToken;

    public ConnectPayload(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
