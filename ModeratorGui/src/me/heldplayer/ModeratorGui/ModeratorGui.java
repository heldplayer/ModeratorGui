package me.heldplayer.ModeratorGui;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import me.heldplayer.ModeratorGui.tables.Issues;

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
		setupDatabase();
		
		isRunning = true;

		getLogger().info("Enabled!");
	}
	
	private void setupDatabase(){
		try {
            getDatabase().find(Issues.class).findRowCount();
        } catch (PersistenceException ex) {
        	getLogger().info("Installing database due to first time usage");
            installDDL();
        }
	}
	
	@Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Issues.class);
        return list;
    }
}
