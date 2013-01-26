
package me.heldplayer.ModeratorGui;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import me.heldplayer.ModeratorGui.WebGui.InternalServerException;
import me.heldplayer.ModeratorGui.WebGui.ThreadWebserver;
import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.SqlUpdate;

public class ModeratorGui extends JavaPlugin {

    public PluginDescriptionFile pdf;
    public List<String> ranks;
    private ThreadWebserver serverThread;
    public static ModeratorGui instance;
    public String[] displayStrings = new String[6];
    public SimpleDateFormat dateFormat;
    private FileConfiguration config = null;
    public static Logger log;
    private HashMap<String, String> passwords;

    @Override
    public void onDisable() {
        serverThread.disconnect();

        getLogger().info("Disabled!");
    }

    @Override
    public void onEnable() {
        instance = this;
        log = this.getLogger();

        try {
            setupDatabase();
        }
        catch (Exception ex) {
            getLogger().severe("Unable to set up the database!");
            getLogger().severe("If you updated from an older version, please follow these steps:");
            getLogger().severe("1. Downgrade to the previous version of ModeratorGui");
            getLogger().severe("2. Use the command '/moderatorgui export'");
            getLogger().severe("3. Update back to the latest version of ModeratorGui for your craftbukkit version");
            getLogger().severe("4. Use the command '/moderatorgui import'");
            getLogger().severe("If the problem persists or you have not yet used ModeratorGui, please post the following in the official BukkitDev plugin page: ");
            ex.printStackTrace();
        }

        pdf = getDescription();

        config = getConfig();

        FileConfiguration defConfig = YamlConfiguration.loadConfiguration(getResource("config.yml"));

        ranks = config.getStringList("ranks");

        if (!config.isSet("config-version")) {
            config.set("config-version", 1);
        }

        if (config.getInt("config-version") < 2) {
            config.set("config-version", 2);

            getServer().getConsoleSender().sendMessage("[" + pdf.getPrefix() + "] " + ChatColor.LIGHT_PURPLE + "Updating table `mgui_issues` for ModeratorGui 1.2");

            SqlUpdate update = getDatabase().createSqlUpdate("ALTER TABLE `mgui_issues` ADD `is_closed` BOOLEAN NOT NULL DEFAULT '0' AFTER `issue`");

            update.execute();
        }

        if (config.getInt("config-version") < 3) {
            config.set("config-version", 3);

            getServer().getConsoleSender().sendMessage("[" + pdf.getPrefix() + "] " + ChatColor.LIGHT_PURPLE + "Updating config file for for ModeratorGui 1.2");

            config.set("messages", defConfig.get("messages"));
        }

        if (config.getInt("config-version") < 4) {
            config.set("config-version", 4);

            getServer().getConsoleSender().sendMessage("[" + pdf.getPrefix() + "] " + ChatColor.LIGHT_PURPLE + "Updating config file for for ModeratorGui 1.3");

            config.set("perform", defConfig.get("perform"));
        }

        if (config.getInt("config-version") < 5) {
            config.set("config-version", 5);

            getServer().getConsoleSender().sendMessage("[" + pdf.getPrefix() + "] " + ChatColor.LIGHT_PURPLE + "Updating config file for for ModeratorGui 1.4");

            config.set("enable-webserver", defConfig.get("enable-webserver"));
        }

        if (config.getInt("config-version") < 6) {
            config.set("config-version", 6);

            RuntimeException ex = new RuntimeException("Please export your database with ModeratorGui version 1.6, and let it uninstall the database. Then import the database with the latest version.");

            ex.fillInStackTrace();

            ex.printStackTrace();
        }

        if (config.getInt("config-version") < 7) {
            config.set("config-version", 7);

            getServer().getConsoleSender().sendMessage("[" + pdf.getPrefix() + "] " + ChatColor.LIGHT_PURPLE + "Updating config file for for ModeratorGui 1.8");

            config.set("accounts", defConfig.get("accounts"));
        }

        displayStrings[0] = config.getString("messages.issue");
        displayStrings[1] = config.getString("messages.resolved-issue");
        displayStrings[2] = config.getString("messages.promotion");
        displayStrings[3] = config.getString("messages.demotion");
        displayStrings[4] = config.getString("messages.ban");
        displayStrings[5] = config.getString("messages.unban");

        this.passwords = new HashMap<String, String>();

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(e);
        }

        ConfigurationSection section = config.getConfigurationSection("accounts");
        Set<String> keys = section.getKeys(false);
        Iterator<String> i = keys.iterator();

        while (i.hasNext()) {
            String username = i.next();

            String password = section.getString(username, "null");

            try {
                md.update(password.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            passwords.put(username.toLowerCase(), hashtext);
        }

        saveConfig();

        serverThread = new ThreadWebserver(config.getInt("port", 8273), config.getString("host", ""));

        if (config.getBoolean("enable-webserver")) {
            serverThread.startup();
        }

        getServer().getPluginManager().registerEvents(new ModGuiListener(this), this);

        getCommand("report").setExecutor(new ReportCommand(this));
        getCommand("review").setExecutor(new ReviewCommand(this));
        getCommand("moderatorgui").setExecutor(new AdminCommand(this));

        dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        getLogger().info("Enabled!");
    }

    protected void setupDatabase() {
        try {
            getDatabase().find(Issues.class).findRowCount();
            getDatabase().find(Bans.class).findRowCount();
            getDatabase().find(Unbans.class).findRowCount();
            getDatabase().find(Promotions.class).findRowCount();
            getDatabase().find(Demotions.class).findRowCount();
            getDatabase().find(Lists.class).findRowCount();
        }
        catch (PersistenceException ex) {
            getLogger().info("Installing database...");

            installDDL();
        }
    }

    protected void deleteDatabase() {
        removeDDL();
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

    public String formatReport(String patern, int id, String target, String reporter, String reason, long date, String oldRank, String newRank) {
        String result = patern;

        result = result.replaceAll("%id%", id + "");
        result = result.replaceAll("%target%", target);
        result = result.replaceAll("%reporter%", reporter);
        result = result.replaceAll("%reason%", reason);
        result = result.replaceAll("%date%", dateFormat.format(Long.valueOf(date)));
        result = result.replaceAll("%oldrank%", oldRank);
        result = result.replaceAll("%newrank%", newRank);

        result = ChatColor.translateAlternateColorCodes('&', result);

        return result;
    }

    public void performCommands(String section, CommandSender sender, int id, String target, String reporter, String reason, long date, String oldRank, String newRank) {
        List<?> commands = config.getList("perform." + section);

        if (commands == null) {
            return;
        }

        for (Object commandObj : commands) {
            String command = (String) commandObj;

            command = formatReport(command, id, target, reporter, reason, date, oldRank, newRank);

            if (command.startsWith("C:")) {
                getServer().dispatchCommand(getServer().getConsoleSender(), command.substring(2));
            }
            else if (command.startsWith("P:")) {
                if (sender instanceof Player) {
                    getServer().dispatchCommand(sender, command.substring(2));
                }
                else {
                    getServer().dispatchCommand(sender, command.substring(3));
                }
            }
            else {
                getServer().dispatchCommand(sender, command);
            }
        }
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
            if (player.getName().substring(0, name.length()).equalsIgnoreCase(name)) {
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

    public static String getPasswordForUsername(String username) {
        username = username.toLowerCase();

        return instance.passwords.get(username);
    }
}
