package net.chess_platform.chess_service.ws.message.client;

public class AuthenticatePayload {
    
    private String accessToken;

    public AuthenticatePayload() {}

    public AuthenticatePayload(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
