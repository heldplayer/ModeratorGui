package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class ReportedResponse extends WebResponse {
	private String request;

	public ReportedResponse(String request) {
		this.request = request;
	}

	@Override
	public void writeResponse(DataOutputStream stream, RequestFlags flags) throws IOException {
		stream.writeBytes("HTTP/1.0 200 Ok\r\n");
		stream.writeBytes("Connection: close\r\n");
		stream.writeBytes("Server: ModeratorGui\r\n");
		stream.writeBytes("Content-Type: text/plain\r\n");
		stream.writeBytes("\r\n");

		Integer i = 0;

		TreeMap<Integer, String> map = new TreeMap<Integer, String>();

		map.put(i++, "{ type: \"promote\", reporter: \"" + request + "\", reported: \"hafnium\", time: \"Today\", reason: \"LOL SPAM => From 1 to 2\" }");
		map.put(i++, "{ type: \"unban\", reporter: \"" + request + "\", reported: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");
		map.put(i++, "{ type: \"ban\", reporter: \"" + request + "\", reported: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");
		map.put(i++, "{ type: \"demote\", reporter: \"" + request + "\", reported: \"hafnium\", time: \"Today\", reason: \"LOL SPAM => From 2 to 1\" }");
		map.put(i++, "{ type: \"issue\", reporter: \"" + request + "\", reported: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");

		String result = "[ ";
		for (Integer I = 0; I < map.size(); I++) {
			result += (I != 0 ? ", " : "") + map.get(I);
		}
		result += " ]";

		stream.writeBytes(result);

	}
}
