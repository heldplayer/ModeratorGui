package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeMap;

public class ReporterResponse extends WebResponse {
	private String request;

	public ReporterResponse(String request) {
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

		map.put(i++, "{ type: \"promote\", reported: \"" + request + "\", reporter: \"hafnium\", time: \"Today\", reason: \"LOL SPAM => From 1 to 2\" }");
		map.put(i++, "{ type: \"unban\", reported: \"" + request + "\", reporter: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");
		map.put(i++, "{ type: \"ban\", reported: \"" + request + "\", reporter: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");
		map.put(i++, "{ type: \"demote\", reported: \"" + request + "\", reporter: \"hafnium\", time: \"Today\", reason: \"LOL SPAM => From 2 to 1\" }");
		map.put(i++, "{ type: \"issue\", reported: \"" + request + "\", reporter: \"hafnium\", time: \"Today\", reason: \"LOL SPAM\" }");

		String result = "[ ";
		for (Integer I = 0; I < map.size(); I++) {
			result += (I != 0 ? ", " : "") + map.get(I);
		}
		result += " ]";

		stream.writeBytes(result);

	}
}
