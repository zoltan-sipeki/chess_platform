package net.chess_platform.chess_service.ws.message.client;

public class JoinMatchPayload {
	
	private String token;

	public JoinMatchPayload() {}

	public JoinMatchPayload(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
