package me.heldplayer.ModeratorGui;

import java.text.SimpleDateFormat;

import me.heldplayer.ModeratorGui.tables.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReviewCommand implements CommandExecutor {

	private final ModeratorGui main;
	private final SimpleDateFormat dateFormat;

	public ReviewCommand(ModeratorGui plugin) {
		main = plugin;

		dateFormat = new SimpleDateFormat("MM-dd-yyyy");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length <= 0) {
			int rowCount = main.getDatabase().find(Lists.class).findRowCount();

			String[] results = new String[13];

			results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
			results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
			results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

			int sideI = 3;
			for (int i = rowCount; i > (rowCount <= 10 ? 0 : rowCount - 10); i--) {
				Lists list = main.getDatabase().find(Lists.class).where().eq("id", i).findUnique();

				if (list == null) {
					continue;
				}

				int id = list.getReportId();
				ReportType type = ReportType.getType(list.getType());

				switch (type) {
				case ISSUE:
					Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " at " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
					break;
				case BAN:
					Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " at " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
					break;
				case UNBAN:
					Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " at " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
					break;
				case PROMOTE:
					Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " at " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
					break;
				case DEMOTE:
					Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.GREEN + " at " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
					break;
				default:
					results[sideI] = ChatColor.DARK_GRAY + "Unspecified action happened";
					break;
				}

				sideI++;
			}

			for (String result : results) {
				if (result != null)
					sender.sendMessage(result);
			}

			return true;
		}

		return false;
	}
}
