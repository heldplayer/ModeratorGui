package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import me.heldplayer.ModeratorGui.ModeratorGui;
import me.heldplayer.ModeratorGui.ReportType;
import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

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

		List<Lists> lists = ModeratorGui.instance.getDatabase().find(Lists.class).where().like("reporter", request + "*").setMaxRows(10).orderBy("id DESC").findList();

		// TODO: finish this
		for (Lists list : lists) {
			int id = list.getReportId();
			ReportType type = ReportType.getType(list.getType());
			
			switch (type) {
			case ISSUE:
				Issues issue = ModeratorGui.instance.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

				map.put(i++, "{ type: \"issue\", reporter }");
				break;
			case BAN:
				Bans ban = ModeratorGui.instance.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

				map.put(i++, "");
				break;
			case UNBAN:
				Unbans unban = ModeratorGui.instance.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

				map.put(i++, "");
				break;
			case PROMOTE:
				Promotions promote = ModeratorGui.instance.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

				map.put(i++, "");
				break;
			case DEMOTE:
				Demotions demote = ModeratorGui.instance.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

				map.put(i++, "");
				break;
			default:
				map.put(i++, "");
				break;
			}
		}

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
