package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;

public class LoginResponse extends WebResponse {
	private String request;

	public LoginResponse(String request) {
		this.request = request;
	}

	@Override
	public void writeResponse(DataOutputStream stream, RequestFlags flags) throws IOException {
		stream.writeBytes("HTTP/1.0 200 Ok\r\n");
		stream.writeBytes("Connection: close\r\n");
		stream.writeBytes("Server: ModeratorGui\r\n");
		stream.writeBytes("Content-Type: text/plain\r\n");
		stream.writeBytes("\r\n");

		//if (request.equals(ModeratorGui.instance.getConfig().getString("web-password"))) {
		if (request.equals("Testificate")) {
			String session = ThreadWebserver.instance.createSession();

			stream.writeBytes(session);
		} else {
			stream.writeBytes("invalid");
		}
	}
}
