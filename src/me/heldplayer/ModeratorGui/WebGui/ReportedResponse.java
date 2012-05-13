package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

import org.json.simple.JSONObject;

public class ReportedResponse extends WebResponse {
	private String request;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

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

		List<Lists> lists = ModeratorGui.instance.getDatabase().find(Lists.class).where().like("target", request + "%").setMaxRows(500).orderBy("id DESC").findList();

		for (Lists list : lists) {
			int id = list.getReportId();
			ReportType type = ReportType.getType(list.getType());

			String result = "";
			switch (type) {
			case ISSUE:
				Issues issue = ModeratorGui.instance.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

				result = "{ ";
				result += "type: \"issue\", ";
				result += "reporter: \"" + JSONObject.escape(issue.getReporter()) + "\", ";
				result += "reported: \"" + JSONObject.escape(issue.getReported()) + "\", ";
				result += "time: \"" + JSONObject.escape(dateFormat.format(Long.valueOf(issue.getTimestamp()))) + "\", ";
				result += "reason: \"" + JSONObject.escape(issue.getIssue()) + "\"";
				result += " }";

				map.put(i++, result);
				break;
			case BAN:
				Bans ban = ModeratorGui.instance.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

				result = "{ ";
				result += "type: \"ban\", ";
				result += "reporter: \"" + JSONObject.escape(ban.getBanner()) + "\", ";
				result += "reported: \"" + JSONObject.escape(ban.getBanned()) + "\", ";
				result += "time: \"" + JSONObject.escape(dateFormat.format(Long.valueOf(ban.getTimestamp()))) + "\", ";
				result += "reason: \"" + JSONObject.escape(ban.getReason()) + "\"";
				result += " }";

				map.put(i++, result);
				break;
			case UNBAN:
				Unbans unban = ModeratorGui.instance.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

				result = "{ ";
				result += "type: \"unban\", ";
				result += "reporter: \"" + JSONObject.escape(unban.getUnbanner()) + "\", ";
				result += "reported: \"" + JSONObject.escape(unban.getUnbanned()) + "\", ";
				result += "time: \"" + JSONObject.escape(dateFormat.format(Long.valueOf(unban.getTimestamp()))) + "\", ";
				result += "reason: \"" + JSONObject.escape(unban.getReason()) + "\"";
				result += " }";

				map.put(i++, result);
				break;
			case PROMOTE:
				Promotions promote = ModeratorGui.instance.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

				result = "{ ";
				result += "type: \"promote\", ";
				result += "reporter: \"" + JSONObject.escape(promote.getPromoter()) + "\", ";
				result += "reported: \"" + JSONObject.escape(promote.getPromoted()) + "\", ";
				result += "time: \"" + JSONObject.escape(dateFormat.format(Long.valueOf(promote.getTimestamp()))) + "\", ";
				result += "prev: \"" + JSONObject.escape(promote.getPrevRank()) + "\", ";
				result += "new: \"" + JSONObject.escape(promote.getNewRank()) + "\", ";
				result += "reason: \"" + JSONObject.escape(promote.getReason()) + "\"";
				result += " }";

				map.put(i++, result);
				break;
			case DEMOTE:
				Demotions demote = ModeratorGui.instance.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

				result = "{ ";
				result += "type: \"demote\", ";
				result += "reporter: \"" + JSONObject.escape(demote.getDemoter()) + "\", ";
				result += "reported: \"" + JSONObject.escape(demote.getDemoted()) + "\", ";
				result += "time: \"" + JSONObject.escape(dateFormat.format(Long.valueOf(demote.getTimestamp()))) + "\", ";
				result += "prev: \"" + JSONObject.escape(demote.getPrevRank()) + "\", ";
				result += "new: \"" + JSONObject.escape(demote.getNewRank()) + "\", ";
				result += "reason: \"" + JSONObject.escape(demote.getReason()) + "\"";
				result += " }";

				map.put(i++, result);
				break;
			default:
				map.put(i++, "");
				break;
			}
		}

		String result = "[ ";
		for (Integer I = 0; I < map.size(); I++) {
			result += (I != 0 ? ", " : "") + map.get(I);
		}
		result += " ]";

		stream.writeBytes(result);
	}
}
