
package me.heldplayer.ModeratorGui;

import java.util.List;

import me.heldplayer.ModeratorGui.tables.Issues;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ModGuiListener implements Listener {

    private ModeratorGui main;

    public ModGuiListener(ModeratorGui plugin) {
        main = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<Issues> issues = main.getDatabase().find(Issues.class).where().eq("reported", event.getPlayer().getName()).eq("is_closed", false).orderBy("id DESC").findList();

        for (Issues issue : issues) {
            String reportString = main.formatReport(main.displayStrings[0], issue.getId(), issue.getReported(), issue.getReporter(), issue.getIssue(), issue.getTimestamp(), null, null);

            main.getServer().broadcast(reportString, "moderatorgui.viewopenissues");
        }
    }
}
