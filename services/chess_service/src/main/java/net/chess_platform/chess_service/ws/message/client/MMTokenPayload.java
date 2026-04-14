package net.chess_platform.chess_service.ws.message.client;

public class MMTokenPayload {

    private String token;

    public MMTokenPayload() {
    }

    public MMTokenPayload(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
