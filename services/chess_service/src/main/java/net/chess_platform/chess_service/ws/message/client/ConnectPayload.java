package net.chess_platform.chess_service.ws.message.client;

public class ConnectPayload {

    private String matchmakingToken;

    public ConnectPayload() {
    }

    public ConnectPayload(String token) {
        this.matchmakingToken = token;
    }

    public String getMatchmakingToken() {
        return matchmakingToken;
    }

}
