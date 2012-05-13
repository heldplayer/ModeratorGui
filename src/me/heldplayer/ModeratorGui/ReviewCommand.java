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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("moderatorgui.review")) {
			sender.sendMessage(ChatColor.RED + "You don't have permissions for this!");
			return true;
		}

		if (args.length <= 0) {
			String[] results = new String[13];

			results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
			results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
			results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

			List<Lists> lists = main.getDatabase().find(Lists.class).setMaxRows(10).orderBy("id DESC").findList();

			int sideI = 3;
			for (Lists list : lists) {
				if (list == null) {
					continue;
				}

				int id = list.getReportId();
				ReportType type = ReportType.getType(list.getType());

				switch (type) {
				case ISSUE:
					Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
					break;
				case BAN:
					Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
					break;
				case UNBAN:
					Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
					break;
				case PROMOTE:
					Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
					break;
				case DEMOTE:
					Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

					results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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

		pagination: {
			if (args.length == 1) {
				int page = 0;

				try {
					page = Integer.parseInt(args[0]) - 1;
				} catch (NumberFormatException ex) {
					break pagination;
				}

				String[] results = new String[13];

				results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
				results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
				results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

				int sideI = 3;

				List<Lists> lists = main.getDatabase().find(Lists.class).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

				for (Lists list : lists) {

					if (list == null) {
						continue;
					}

					int id = list.getReportId();
					ReportType type = ReportType.getType(list.getType());

					switch (type) {
					case ISSUE:
						Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
						break;
					case BAN:
						Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
						break;
					case UNBAN:
						Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
						break;
					case PROMOTE:
						Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
						break;
					case DEMOTE:
						Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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

				String[] results = new String[13];

				results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
				results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
				results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

				int sideI = 3;

				List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("reporter", name).setMaxRows(10).orderBy("id DESC").findList();

				for (Lists list : lists) {
					if (list == null) {
						continue;
					}

					int id = list.getReportId();
					ReportType type = ReportType.getType(list.getType());

					switch (type) {
					case ISSUE:
						Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
						break;
					case BAN:
						Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
						break;
					case UNBAN:
						Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
						break;
					case PROMOTE:
						Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
						break;
					case DEMOTE:
						Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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

					String[] results = new String[13];

					results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
					results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
					results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

					int sideI = 3;

					List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("reporter", name).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

					for (Lists list : lists) {
						if (list == null) {
							continue;
						}

						int id = list.getReportId();
						ReportType type = ReportType.getType(list.getType());

						switch (type) {
						case ISSUE:
							Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
							break;
						case BAN:
							Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
							break;
						case UNBAN:
							Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
							break;
						case PROMOTE:
							Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
							break;
						case DEMOTE:
							Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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

				String[] results = new String[13];

				results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
				results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
				results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

				int sideI = 3;

				List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("target", name).setMaxRows(10).orderBy("id DESC").findList();

				for (Lists list : lists) {
					if (list == null) {
						continue;
					}

					int id = list.getReportId();
					ReportType type = ReportType.getType(list.getType());

					switch (type) {
					case ISSUE:
						Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
						break;
					case BAN:
						Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
						break;
					case UNBAN:
						Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
						break;
					case PROMOTE:
						Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
						break;
					case DEMOTE:
						Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

						results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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

					String[] results = new String[13];

					results[0] = ChatColor.GRAY + "Types: " + ChatColor.YELLOW + "Issue " + ChatColor.DARK_RED + "Ban " + ChatColor.DARK_GREEN + "Unban " + ChatColor.GREEN + "Promote " + ChatColor.RED + "Demote";
					results[1] = ChatColor.GRAY + "Current date: " + dateFormat.format(Long.valueOf(System.currentTimeMillis()));
					results[2] = ChatColor.GRAY + "" + ChatColor.ITALIC + "All dates are MM-dd-yyyy";

					int sideI = 3;

					List<Lists> lists = main.getDatabase().find(Lists.class).where().eq("target", name).setMaxRows(10).setFirstRow(page * 10).orderBy("id DESC").findList();

					for (Lists list : lists) {
						if (list == null) {
							continue;
						}

						int id = list.getReportId();
						ReportType type = ReportType.getType(list.getType());

						switch (type) {
						case ISSUE:
							Issues issue = main.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.YELLOW + "[X] " + ChatColor.AQUA + issue.getReported() + ChatColor.YELLOW + ", by " + ChatColor.AQUA + issue.getReporter() + ChatColor.YELLOW + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(issue.getTimestamp())) + ChatColor.YELLOW + ": " + issue.getIssue();
							break;
						case BAN:
							Bans ban = main.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.DARK_RED + "[X] " + ChatColor.AQUA + ban.getBanned() + ChatColor.DARK_RED + ", by " + ChatColor.AQUA + ban.getBanner() + ChatColor.DARK_RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(ban.getTimestamp())) + ChatColor.DARK_RED + ": " + ban.getReason();
							break;
						case UNBAN:
							Unbans unban = main.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.DARK_GREEN + "[X] " + ChatColor.AQUA + unban.getUnbanned() + ChatColor.DARK_GREEN + ", by " + ChatColor.AQUA + unban.getUnbanner() + ChatColor.DARK_GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(unban.getTimestamp())) + ChatColor.DARK_GREEN + ": " + unban.getReason();
							break;
						case PROMOTE:
							Promotions promote = main.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.GREEN + "[X] " + ChatColor.AQUA + promote.getPromoted() + ChatColor.GREEN + ", by " + ChatColor.AQUA + promote.getPromoter() + ChatColor.GREEN + ", " + ChatColor.AQUA + promote.getPrevRank() + ChatColor.GREEN + " => " + ChatColor.AQUA + promote.getNewRank() + ChatColor.GREEN + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(promote.getTimestamp())) + ChatColor.GREEN + ": " + promote.getReason();
							break;
						case DEMOTE:
							Demotions demote = main.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

							results[sideI] = ChatColor.RED + "[X] " + ChatColor.AQUA + demote.getDemoted() + ChatColor.RED + ", by " + ChatColor.AQUA + demote.getDemoter() + ChatColor.RED + ", " + ChatColor.AQUA + demote.getPrevRank() + ChatColor.RED + " => " + ChatColor.AQUA + demote.getNewRank() + ChatColor.RED + " on " + ChatColor.AQUA + dateFormat.format(Long.valueOf(demote.getTimestamp())) + ChatColor.RED + ": " + demote.getReason();
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
			}
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
