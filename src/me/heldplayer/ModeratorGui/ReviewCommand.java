package me.heldplayer.ModeratorGui;

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

	public ReviewCommand(ModeratorGui plugin) {
		main = plugin;
	}

	private void displayLists(CommandSender sender, List<Lists> lists) {
		for (Lists list : lists) {
			if (list == null) {
				continue;
			}

			int id = list.getReportId();
			ReportType type = ReportType.getType(list.getType());

			switch (type) {
			case ISSUE:
				Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

				if (issue.isClosed()) {
					sender.sendMessage(main.formatReport(main.displayStrings[1], issue.getId(), issue.getReported(), issue.getReporter(), issue.getIssue(), issue.getTimestamp(), null, null));
				} else {
					sender.sendMessage(main.formatReport(main.displayStrings[0], issue.getId(), issue.getReported(), issue.getReporter(), issue.getIssue(), issue.getTimestamp(), null, null));
				}
				break;
			case BAN:
				Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

				sender.sendMessage(main.formatReport(main.displayStrings[4], ban.getId(), ban.getBanned(), ban.getBanner(), ban.getReason(), ban.getTimestamp(), null, null));
				break;
			case UNBAN:
				Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

				sender.sendMessage(main.formatReport(main.displayStrings[5], unban.getId(), unban.getUnbanned(), unban.getUnbanner(), unban.getReason(), unban.getTimestamp(), null, null));
				break;
			case PROMOTE:
				Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

				sender.sendMessage(main.formatReport(main.displayStrings[2], promote.getId(), promote.getPromoted(), promote.getPromoter(), promote.getReason(), promote.getTimestamp(), promote.getPrevRank(), promote.getNewRank()));
				break;
			case DEMOTE:
				Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

				sender.sendMessage(main.formatReport(main.displayStrings[3], demote.getId(), demote.getDemoted(), demote.getDemoter(), demote.getReason(), demote.getTimestamp(), demote.getPrevRank(), demote.getNewRank()));
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
			sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
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
				sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
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
				sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
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
					sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
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
				sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
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
					sender.sendMessage(ChatColor.GRAY + "Current date: " + main.dateFormat.format(Long.valueOf(System.currentTimeMillis())));
					sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "All dates are MM-dd-yyyy");

					List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("target", name).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

					displayLists(sender, lists);

					return true;
				}
			}
		}

		if (args[0].equalsIgnoreCase("close")) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "Not enough arguments to close reports");
				return true;
			}

			for (int i = 1; i < args.length; i++) {
				String[] idInfo = args[i].split(":");

				if (idInfo.length != 2) {
					sender.sendMessage(ChatColor.RED + "Argument '" + args[i] + "' is not in a valid format");
					continue;
				}

				int id = 0;

				try {
					id = Integer.parseInt(idInfo[1]);
				} catch (NumberFormatException ex) {
					sender.sendMessage(ChatColor.RED + "'" + idInfo[1] + "' is not a valid number");
					continue;
				}

				if (idInfo[0].equalsIgnoreCase("I")) {
					Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

					issue.setClosed(true);

					main.getDatabase().update(issue);

					sender.sendMessage(ChatColor.GREEN + "Closed issue id '" + ChatColor.AQUA + id + ChatColor.GREEN + "' by " + ChatColor.AQUA + issue.getReporter() + ChatColor.GREEN + ": " + ChatColor.AQUA + issue.getIssue());

					continue;
				}

				sender.sendMessage(ChatColor.RED + "'" + idInfo[0] + "' is not a valid report type");
				sender.sendMessage(ChatColor.RED + ChatColor.ITALIC.toString() + "Valid report types: I");
			}

			return true;
		}

		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " [page]");
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " by <playername> [page]");
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " target <playername> [page]");
			sender.sendMessage(ChatColor.GRAY + "/" + alias + " close I:<id> [I:<id> [...]]");

			return true;
		}

		return false;
	}
}
