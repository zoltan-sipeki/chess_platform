package net.chess_platform.chess_service.ws.message.client;

public class ConnectPayload {

    private String accessToken;

    public ConnectPayload(String accessToken) {
        this.accessToken = accessToken;
    }

    public ConnectPayload() {
    }

    public String getAccessToken() {
        return accessToken;
    }

}
