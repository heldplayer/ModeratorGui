
package me.heldplayer.ModeratorGui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final ModeratorGui main;

    public AdminCommand(ModeratorGui plugin) {
        main = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (sender.hasPermission("moderatorgui.export"))
                sender.sendMessage(ChatColor.GRAY + "/" + alias + " export");
            if (sender.hasPermission("moderatorgui.import"))
                sender.sendMessage(ChatColor.GRAY + "/" + alias + " import " + ChatColor.DARK_RED + "WARNING: Clears the current database and replaces it with the contents of 'data.bin'");
            if (sender.hasPermission("moderatorgui.uninstall"))
                sender.sendMessage(ChatColor.GRAY + "/" + alias + " uninstall " + ChatColor.DARK_RED + "WARNING: Deletes database and disables the plugin, the plugin will need to be manually removed after server shutdown");
            if (sender.hasPermission("moderatorgui.setpass") && sender instanceof Player && Bukkit.getOnlineMode())
                sender.sendMessage(ChatColor.GRAY + "/" + alias + " setpass <password> " + ChatColor.DARK_RED + "WARNING: Password will be visible in console, do not use a password you use anywhere else!");

            return true;
        }

        if (args[0].equalsIgnoreCase("export") && sender.hasPermission("moderatorgui.export")) {
            try {
                sender.sendMessage(ChatColor.GRAY + "Exporting database to /plugins/ModeratorGui/data.bin");

                List<Lists> lists = main.getDatabase().find(Lists.class).findList();

                FileOutputStream FOS = new FileOutputStream(new File(main.getDataFolder(), "data.bin"));

                DataOutputStream DOS = new DataOutputStream(FOS);

                DOS.writeInt(lists.size());

                for (Lists list : lists) {
                    int id = list.getReportId();
                    ReportType type = ReportType.getType(list.getType());

                    DOS.writeInt(list.getId());

                    DOS.writeInt(list.getReportId());

                    DOS.writeInt(list.getType());

                    switch (type) {
                    case ISSUE:
                        Issues issue = ModeratorGui.instance.getDatabase().find(Issues.class).where().eq("id", id).findUnique();

                        issue.toData(DOS);
                    break;
                    case BAN:
                        Bans ban = ModeratorGui.instance.getDatabase().find(Bans.class).where().eq("id", id).findUnique();

                        ban.toData(DOS);
                    break;
                    case UNBAN:
                        Unbans unban = ModeratorGui.instance.getDatabase().find(Unbans.class).where().eq("id", id).findUnique();

                        unban.toData(DOS);
                    break;
                    case PROMOTE:
                        Promotions promote = ModeratorGui.instance.getDatabase().find(Promotions.class).where().eq("id", id).findUnique();

                        promote.toData(DOS);
                    break;
                    case DEMOTE:
                        Demotions demote = ModeratorGui.instance.getDatabase().find(Demotions.class).where().eq("id", id).findUnique();

                        demote.toData(DOS);
                    break;
                    default:
                    break;
                    }
                }

                DOS.flush();
                DOS.close();
                FOS.close();

                sender.sendMessage(ChatColor.GREEN + "Data exported!");

            }
            catch (FileNotFoundException e) {
                sender.sendMessage(ChatColor.RED + "Failed exporting data to binary file, see the console for more information");

                e.printStackTrace();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Failed exporting data to binary file, see the console for more information");

                e.printStackTrace();
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("import") && sender.hasPermission("moderatorgui.import")) {
            try {
                FileInputStream FIS = new FileInputStream(new File(main.getDataFolder(), "data.bin"));

                DataInputStream DIS = new DataInputStream(FIS);

                sender.sendMessage(ChatColor.GRAY + "Deleting database...");
                main.deleteDatabase();
                sender.sendMessage(ChatColor.GRAY + "Setting up database...");
                main.setupDatabase();
                sender.sendMessage(ChatColor.GRAY + "Importing from /plugins/ModeratorGui/data.bin");

                int rowCount = DIS.readInt();

                for (int i = 0; i < rowCount; i++) {
                    int id = DIS.readInt();
                    int reportId = DIS.readInt();
                    int reportType = DIS.readInt();
                    String reporter, target;

                    ReportType type = ReportType.getType(reportType);

                    switch (type) {
                    case ISSUE:
                        Issues issue = Issues.fromData(DIS);

                        reporter = issue.getReporter();
                        target = issue.getReported();

                        main.getDatabase().insert(issue);
                    break;
                    case BAN:
                        Bans ban = Bans.fromData(DIS);

                        reporter = ban.getReporter();
                        target = ban.getReported();

                        main.getDatabase().insert(ban);
                    break;
                    case UNBAN:
                        Unbans unban = Unbans.fromData(DIS);

                        reporter = unban.getReporter();
                        target = unban.getReported();

                        main.getDatabase().insert(unban);
                    break;
                    case PROMOTE:
                        Promotions promote = Promotions.fromData(DIS);

                        reporter = promote.getReporter();
                        target = promote.getReported();

                        main.getDatabase().insert(promote);
                    break;
                    case DEMOTE:
                        Demotions demote = Demotions.fromData(DIS);

                        reporter = demote.getReporter();
                        target = demote.getReported();

                        main.getDatabase().insert(demote);
                    break;
                    default:
                        continue;
                    }

                    Lists list = new Lists();

                    list.setId(id);
                    list.setReportId(reportId);
                    list.setType(reportType);
                    list.setReporter(reporter);
                    list.setTarget(target);

                    main.getDatabase().insert(list);
                }

                DIS.close();
                FIS.close();

                sender.sendMessage(ChatColor.GREEN + "Data imported!");

            }
            catch (FileNotFoundException e) {
                sender.sendMessage(ChatColor.RED + "Failed importing data from binary file, see the console for more information");

                e.printStackTrace();
            }
            catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Failed importing data from binary file, see the console for more information");

                e.printStackTrace();
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("uninstall") && sender.hasPermission("moderatorgui.uninstall")) {
            sender.sendMessage(ChatColor.RED + "Deleting database...");
            main.getLogger().info("Deleting database by user command, command executed by " + sender.getName());

            main.deleteDatabase();

            sender.sendMessage(ChatColor.RED + "Disabling ModeratorGui");
            sender.sendMessage(ChatColor.DARK_RED + "WARNING: ModeratorGui will be enabled again after server restart, replace or delete the jar before restarting the server!");
            main.getLogger().info("Disabling self...");

            Bukkit.getPluginManager().disablePlugin(main);

            return true;
        }

        if (args[0].equalsIgnoreCase("setpass") && sender.hasPermission("moderatorgui.setpass") && args.length >= 2 && sender instanceof Player) {
            if (!Bukkit.getOnlineMode()) {
                sender.sendMessage(ChatColor.RED + "The server is running in offline mode! Passwords can only be set when the server is in online mode to protect the server from malicious users");
            }

            String password = args[1];

            for (int i = 2; i < args.length; i++) {
                password += " " + args[i];
            }

            try {
                ModeratorGui.md.update(password.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] digest = ModeratorGui.md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            if (ModeratorGui.instance.passwords.containsKey(sender.getName().toLowerCase())) {
                ModeratorGui.instance.passwords.remove(sender.getName().toLowerCase());
            }

            ModeratorGui.instance.passwords.put(sender.getName().toLowerCase(), hashtext);

            ModeratorGui.instance.config.set("accounts." + sender.getName(), hashtext);

            ModeratorGui.instance.saveConfig();

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> result = new ArrayList<String>();

        if (args.length == 1) {
            if (sender.hasPermission("moderatorgui.export")) {
                result.add("export");
            }

            result.add("help");

            if (sender.hasPermission("moderatorgui.import")) {
                result.add("import");
            }

            if (sender.hasPermission("moderatorgui.setpass")) {
                result.add("setpass");
            }

            if (sender.hasPermission("moderatorgui.uninstall")) {
                result.add("uninstall");
            }

        }

        return result;
    }

}
