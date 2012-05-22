package me.heldplayer.ModeratorGui;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import me.heldplayer.ModeratorGui.WebGui.ThreadWebserver;
import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ModeratorGui extends JavaPlugin {

	public PluginDescriptionFile pdf;
	public List<String> ranks;
	private ThreadWebserver serverThread;
	public static ModeratorGui instance;

	@Override
	public void onDisable() {
		serverThread.disconnect();

		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable() {
		instance = this;

		setupDatabase();

		pdf = getDescription();

		FileConfiguration config = getConfig();

		ranks = config.getStringList("ranks");

		getCommand("report").setExecutor(new ReportCommand(this));
		getCommand("review").setExecutor(new ReviewCommand(this));

		serverThread = new ThreadWebserver(config.getInt("port", 8273),
				config.getString("host", ""));

		saveConfig();

		getLogger().info("Enabled!");
	}

	private void setupDatabase() {
		try {
			getDatabase().find(Issues.class).findRowCount();
			getDatabase().find(Bans.class).findRowCount();
			getDatabase().find(Unbans.class).findRowCount();
			getDatabase().find(Promotions.class).findRowCount();
			getDatabase().find(Demotions.class).findRowCount();
			getDatabase().find(Lists.class).findRowCount();
		} catch (PersistenceException ex) {
			getLogger().info("Installing database due to first time usage");
			try {
				installDDL();
			} catch (Exception ex2) {
				getLogger().severe("Unable to set up the database!");
			}
		}
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Issues.class);
		list.add(Bans.class);
		list.add(Unbans.class);
		list.add(Promotions.class);
		list.add(Demotions.class);
		list.add(Lists.class);
		return list;
	}

	public static List<String> getPlayerMatches(String name) {
		OfflinePlayer[] players = instance.getServer().getOfflinePlayers();

		List<String> matched = new ArrayList<String>();

		for (OfflinePlayer player : players) {
			if (player.getName().equalsIgnoreCase(name)) {
				matched.clear();
				matched.add(player.getName());
				return matched;
			}
			if (player.getName().length() < name.length()) {
				continue;
			}
			if (player.getName().substring(0, name.length()).equalsIgnoreCase(
					name)) {
				matched.add(player.getName());
			}
		}

		return matched;
	}

	public static List<String> getRankMatches(String rank) {
		List<String> ranks = instance.ranks;

		List<String> matched = new ArrayList<String>();

		for (String matchedRank : ranks) {
			if (matchedRank.equalsIgnoreCase(rank)) {
				matched.clear();
				matched.add(matchedRank);
				return matched;
			}
			if (matchedRank.length() < rank.length()) {
				continue;
			}
			if (matchedRank.substring(0, rank.length()).equalsIgnoreCase(rank)) {
				matched.add(matchedRank);
			}
		}

		return matched;
	}
}
