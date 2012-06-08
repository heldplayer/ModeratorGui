package me.heldplayer.ModeratorGui;

import java.text.SimpleDateFormat;
import java.util.List;

import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

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

	private void displayLists(CommandSender sender, List<Lists> lists) {
		for (Lists list : lists) {
			if (list == null) {
				continue;
			}

			int id = list.getReportId();
			ReportType type = ReportType.getType(list.getType());

			String dataColor = "";
			String infoColor = "";

			switch (type) {
			case ISSUE:
				Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

				dataColor = ChatColor.AQUA.toString();
				infoColor = ChatColor.YELLOW.toString();

				if (issue.isClosed()) {
					dataColor += ChatColor.STRIKETHROUGH.toString();
					infoColor += ChatColor.STRIKETHROUGH.toString();
				}

				sender.sendMessage(dataColor + issue.getReported() + infoColor + ", by " + dataColor + issue.getReporter() + infoColor + " on " + dataColor + dateFormat.format(Long.valueOf(issue.getTimestamp())) + infoColor + ": " + issue.getIssue());
				break;
			case BAN:
				Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

				dataColor = ChatColor.AQUA.toString();
				infoColor = ChatColor.DARK_RED.toString();

				sender.sendMessage(dataColor + ban.getBanned() + infoColor + ", by " + dataColor + ban.getBanner() + infoColor + " on " + dataColor + dateFormat.format(Long.valueOf(ban.getTimestamp())) + infoColor + ": " + ban.getReason());
				break;
			case UNBAN:
				Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

				dataColor = ChatColor.AQUA.toString();
				infoColor = ChatColor.DARK_GREEN.toString();

				sender.sendMessage(dataColor + unban.getUnbanned() + infoColor + ", by " + dataColor + unban.getUnbanner() + infoColor + " on " + dataColor + dateFormat.format(Long.valueOf(unban.getTimestamp())) + infoColor + ": " + unban.getReason());
				break;
			case PROMOTE:
				Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

				dataColor = ChatColor.AQUA.toString();
				infoColor = ChatColor.GREEN.toString();

				sender.sendMessage(dataColor + promote.getPromoted() + infoColor + ", by " + dataColor + promote.getPromoter() + infoColor + ", " + dataColor + promote.getPrevRank() + infoColor + " => " + dataColor + promote.getNewRank() + infoColor + " on " + dataColor + dateFormat.format(Long.valueOf(promote.getTimestamp())) + infoColor + ": " + promote.getReason());
				break;
			case DEMOTE:
				Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

				dataColor = ChatColor.AQUA.toString();
				infoColor = ChatColor.RED.toString();

				sender.sendMessage(dataColor + demote.getDemoted() + infoColor + ", by " + dataColor + demote.getDemoter() + infoColor + ", " + dataColor + demote.getPrevRank() + infoColor + " => " + dataColor + demote.getNewRank() + infoColor + " on " + dataColor + dateFormat.format(Long.valueOf(demote.getTimestamp())) + infoColor + ": " + demote.getReason());
				break;
			default:
				sender.sendMessage(ChatColor.DARK_GRAY + "Unspecified action happened");
				break;
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("moderatorgui.review")) {
			sender.sendMessage(ChatColor.RED + "You don't have permissions for this!");
			return true;
		}

		if (args.length <= 0) {
			sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
			sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
			sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

			List<Lists> lists = main.getDatabase().find(Lists.class).setMaxRows(10).orderBy("id DESC").findList();

			displayLists(sender, lists);

			return true;
		}

		pagination: {
			if (args.length == 1) {
				int page = 0;

				try {
					page = Integer.parseInt(args[0]) - 1;
				} catch (NumberFormatException ex) {
					break pagination;
				}

				sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
				sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
				sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

				List<Lists> lists = main.getDatabase().find(Lists.class).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

				displayLists(sender, lists);

				return true;
			}
		}

		if (args[0].equalsIgnoreCase("by")) {
			if (args.length == 2) {
				List<String> matchedNames = ModeratorGui.getPlayerMatches(args[1]);

				String name = "";

				if (matchedNames.size() == 1) {
					name = matchedNames.get(0);
				} else if (matchedNames.size() == 0) {
					sender.sendMessage(ChatColor.RED + "No match found for '" + args[1] + "'");

					return true;
				} else {
					sender.sendMessage(ChatColor.GREEN + "Multiple matches found: ");

					String matches = "";

					for (String matched : matchedNames) {
						matches += ", " + matched;
					}

					matches = matches.replaceFirst(", ", "").trim();

					sender.sendMessage(ChatColor.GRAY + matches);

					return true;
				}

				sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
				sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
				sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

				List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("reporter", name).setMaxRows(10).orderBy("id DESC").findList();

				displayLists(sender, lists);

				return true;
			} else if (args.length > 2) {
				pagination: {
					int page = 0;

					try {
						page = Integer.parseInt(args[0]) - 1;
					} catch (NumberFormatException ex) {
						break pagination;
					}

					List<String> matchedNames = ModeratorGui.getPlayerMatches(args[1]);

					String name = "";

					if (matchedNames.size() == 1) {
						name = matchedNames.get(0);
					} else if (matchedNames.size() == 0) {
						sender.sendMessage(ChatColor.RED + "No match found for '" + args[1] + "'");

						return true;
					} else {
						sender.sendMessage(ChatColor.GREEN + "Multiple matches found: ");

						String matches = "";

						for (String matched : matchedNames) {
							matches += ", " + matched;
						}

						matches = matches.replaceFirst(", ", "").trim();

						sender.sendMessage(ChatColor.GRAY + matches);

						return true;
					}

					sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
					sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
					sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

					List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("reporter", name).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

					displayLists(sender, lists);

					return true;
				}
			}
		}

		if (args[0].equalsIgnoreCase("target")) {
			if (args.length == 2) {
				List<String> matchedNames = ModeratorGui.getPlayerMatches(args[1]);

				String name = "";

				if (matchedNames.size() == 1) {
					name = matchedNames.get(0);
				} else if (matchedNames.size() == 0) {
					sender.sendMessage(ChatColor.RED + "No match found for '" + args[1] + "'");

					return true;
				} else {
					sender.sendMessage(ChatColor.GREEN + "Multiple matches found: ");

					String matches = "";

					for (String matched : matchedNames) {
						matches += ", " + matched;
					}

					matches = matches.replaceFirst(", ", "").trim();

					sender.sendMessage(ChatColor.GRAY + matches);

					return true;
				}

				sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
				sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
				sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

				List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("target", name).setMaxRows(10).orderBy("id DESC").findList();

				displayLists(sender, lists);

				return true;
			} else if (args.length > 2) {
				pagination: {
					int page = 0;

					try {
						page = Integer.parseInt(args[0]) - 1;
					} catch (NumberFormatException ex) {
						sender.sendMessage(ChatColor.RED + "Unable to parse page");
						break pagination;
					}

					List<String> matchedNames = ModeratorGui.getPlayerMatches(args[1]);

					String name = "";

					if (matchedNames.size() == 1) {
						name = matchedNames.get(0);
					} else if (matchedNames.size() == 0) {
						sender.sendMessage(ChatColor.RED + "No match found for '" + args[1] + "'");

						return true;
					} else {
						sender.sendMessage(ChatColor.GREEN + "Multiple matches found: ");

						String matches = "";

						for (String matched : matchedNames) {
							matches += ", " + matched;
						}

						matches = matches.replaceFirst(", ", "").trim();

						sender.sendMessage(ChatColor.GRAY + matches);

						return true;
					}

					sender.sendMessage(ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote");
					sender.sendMessage(ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis())));
					sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

					List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("target", name).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

					displayLists(sender, lists);

					return true;
				}
			}
		}

		if (args[0].equalsIgnoreCase("close")) {

			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " [page]");
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " by <playername> [page]");
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " target <playername> [page]");

			return true;
		}

		return false;
	}
}
