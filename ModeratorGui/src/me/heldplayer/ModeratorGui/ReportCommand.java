package me.heldplayer.ModeratorGui;

import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Unbans;

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
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("ban") && args.length > 2) {
			String reason = "";

			for (int i = 2; i < args.length; i++) {
				reason += " " + args[i];
			}

			reason = reason.trim();

			Bans banRow = new Bans();
			banRow.setReason(reason);
			banRow.setBanned(args[1]);
			banRow.setBanner(sender.getName());
			banRow.setTimestamp(System.currentTimeMillis());
			
			main.getDatabase().save(banRow);
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("unban") && args.length > 2) {
			String reason = "";

			for (int i = 2; i < args.length; i++) {
				reason += " " + args[i];
			}

			reason = reason.trim();

			Unbans unbanRow = new Unbans();
			unbanRow.setReason(reason);
			unbanRow.setUnbanned(args[1]);
			unbanRow.setUnbanner(sender.getName());
			unbanRow.setTimestamp(System.currentTimeMillis());
			
			main.getDatabase().save(unbanRow);
			
			return true;
		}

		return false;
	}

}
