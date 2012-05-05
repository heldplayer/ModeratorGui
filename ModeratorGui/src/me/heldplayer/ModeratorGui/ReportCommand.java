package me.heldplayer.ModeratorGui;

import me.heldplayer.ModeratorGui.tables.Issues;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReportCommand implements CommandExecutor {

	private final ModeratorGui main;

	public ReportCommand(ModeratorGui plugin) {
		main = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args[0].equalsIgnoreCase("issue") && args.length > 2) {
			String issue = "";

			for (int i = 2; i < args.length; i++) {
				issue += " " + args[i];
			}

			issue = issue.trim();

			Issues issueRow = new Issues();
			issueRow.setIssue(issue);
			issueRow.setReported(args[1]);
			issueRow.setReporter(sender.getName());
			issueRow.setTimestamp(System.currentTimeMillis());
			
			main.getDatabase().save(issueRow);
		}

		return false;
	}

}
