package net.chess_platform.relay_service.ws.message.client;

public class UpdatePreferredStatusPayload {

    private String status;

    public UpdatePreferredStatusPayload(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
