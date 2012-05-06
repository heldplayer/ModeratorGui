package me.heldplayer.ModeratorGui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ModeratorGui extends JavaPlugin {

	public static boolean isRunning = false;
	public PluginDescriptionFile pdf;
	public List<String> ranks;

	@Override
	public void onDisable() {
		isRunning = false;

		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable() {
		setupDatabase();

		pdf = getDescription();
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
		
		List<String> defaultRanks = new ArrayList<String>();
		defaultRanks.add("default");
		defaultRanks.add("mod");
		defaultRanks.add("admin");
		config.addDefault("ranks", defaultRanks);
		
		ranks = config.getStringList("ranks");
		
		try {
			config.save(new File(this.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		getCommand("report").setExecutor(new ReportCommand(this));

		isRunning = true;

		getLogger().info("Enabled!");
	}

	private void setupDatabase() {
		try {
			getDatabase().find(Issues.class).findRowCount();
			getDatabase().find(Bans.class).findRowCount();
			getDatabase().find(Unbans.class).findRowCount();
			getDatabase().find(Promotions.class).findRowCount();
			getDatabase().find(Demotions.class).findRowCount();
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
		return list;
	}
}
