package me.heldplayer.ModeratorGui;

import java.io.File;
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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ModeratorGui extends JavaPlugin {

	public PluginDescriptionFile pdf;
	public List<String> ranks;
	private ThreadWebserver serverThread;

	@Override
	public void onDisable() {
		serverThread.disconnect();

		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable() {
		setupDatabase();

		pdf = getDescription();
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		
		ranks = config.getStringList("ranks");

		getCommand("report").setExecutor(new ReportCommand(this));
		getCommand("review").setExecutor(new ReviewCommand(this));
		
		serverThread = new ThreadWebserver();

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
			installDDL();
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
}
