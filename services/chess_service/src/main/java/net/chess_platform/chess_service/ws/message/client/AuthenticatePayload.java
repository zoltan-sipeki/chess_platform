package net.chess_platform.chess_service.ws.message.client;

public class AuthenticatePayload {

    private String accessToken;

    public AuthenticatePayload(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthenticatePayload() {
    }

    public String getAccessToken() {
        return accessToken;
    }

}
