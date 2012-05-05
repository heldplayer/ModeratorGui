package me.heldplayer.ModeratorGui;

import org.bukkit.plugin.java.JavaPlugin;

public class ModeratorGui extends JavaPlugin {

	public static boolean isRunning = false;

	@Override
	public void onDisable() {
		isRunning = false;

		getLogger().info("Disabled!");
	}

	@Override
	public void onEnable() {
		isRunning = true;

		getLogger().info("Enabled!");
	}
}
