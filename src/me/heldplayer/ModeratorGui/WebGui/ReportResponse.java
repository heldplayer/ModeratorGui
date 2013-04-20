
package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.heldplayer.ModeratorGui.ModeratorGui;
import me.heldplayer.ModeratorGui.ReportType;
import me.heldplayer.ModeratorGui.WebGui.ErrorResponse.ErrorType;
import me.heldplayer.ModeratorGui.tables.Bans;
import me.heldplayer.ModeratorGui.tables.Demotions;
import me.heldplayer.ModeratorGui.tables.Issues;
import me.heldplayer.ModeratorGui.tables.Lists;
import me.heldplayer.ModeratorGui.tables.Promotions;
import me.heldplayer.ModeratorGui.tables.Unbans;

public class ReportResponse extends WebResponse {

    private String session;
    private String type;
    private String reported;
    private String reason;
    private String previousRank;
    private String newRank;
    private final ModeratorGui main;

    public ReportResponse(String session, String type, String reported, String reason, String previousRank, String newRank) throws IOException {
        super();

        this.session = session;
        this.type = type;
        this.reported = reported;
        this.reason = reason;
        this.previousRank = previousRank;
        this.newRank = newRank;
        this.main = ModeratorGui.instance;

        if ("ipdbu".indexOf(this.type) < 0) {
            throw new WebGuiException(new ErrorResponse(ErrorType.NotAcceptable));
        }
    }

    @Override
    public WebResponse writeResponse(RequestFlags flags) throws IOException {
        dop.writeBytes("HTTP/1.0 200 Ok\r\n");
        dop.writeBytes("Connection: close\r\n");
        dop.writeBytes("Server: ModeratorGui\r\n");
        dop.writeBytes("Content-Type: text/plain\r\n");
        dop.writeBytes("\r\n");

        if (this.type.equals("i")) {
            Issues row = new Issues();
            row.setIssue(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());

            main.getDatabase().save(row);

            Issues created = main.getDatabase().find(Issues.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.ISSUE, created.getReporter(), created.getReported());
            main.performCommands("issue", Bukkit.getConsoleSender(), row.getId(), row.getReported(), row.getReporter(), row.getIssue(), row.getTimestamp(), null, null);

            String reportString = main.formatReport(main.displayStrings[0], row.getId(), row.getReported(), row.getReporter(), row.getIssue(), row.getTimestamp(), null, null);

            main.getServer().broadcast(ChatColor.GRAY + row.getReporter() + " (Web) reported a new issue.", "moderatorgui.viewreported");
            main.getServer().broadcast(reportString, "moderatorgui.viewreported");

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("p")) {
            Promotions row = new Promotions();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());
            row.setPrevRank(previousRank);
            row.setNewRank(newRank);

            main.getDatabase().save(row);

            Promotions created = main.getDatabase().find(Promotions.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.PROMOTE, created.getReporter(), created.getReported());
            main.performCommands("promote", Bukkit.getConsoleSender(), row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), row.getPrevRank(), row.getNewRank());

            String reportString = main.formatReport(main.displayStrings[2], row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), row.getPrevRank(), row.getNewRank());

            main.getServer().broadcast(ChatColor.GRAY + created.getReporter() + " reported a new promotion.", "moderatorgui.viewreported");
            main.getServer().broadcast(reportString, "moderatorgui.viewreported");

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("d")) {
            Demotions row = new Demotions();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());
            row.setPrevRank(previousRank);
            row.setNewRank(newRank);

            main.getDatabase().save(row);

            Demotions created = main.getDatabase().find(Demotions.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.DEMOTE, created.getReporter(), created.getReported());
            main.performCommands("demote", Bukkit.getConsoleSender(), row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), row.getPrevRank(), row.getNewRank());

            String reportString = main.formatReport(main.displayStrings[3], row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), row.getPrevRank(), row.getNewRank());

            main.getServer().broadcast(ChatColor.GRAY + created.getReporter() + " reported a new demotion.", "moderatorgui.viewreported");
            main.getServer().broadcast(reportString, "moderatorgui.viewreported");

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("b")) {
            Bans row = new Bans();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());

            main.getDatabase().save(row);

            Bans created = main.getDatabase().find(Bans.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.BAN, created.getReporter(), created.getReported());
            main.performCommands("ban", Bukkit.getConsoleSender(), row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), null, null);

            String reportString = main.formatReport(main.displayStrings[4], row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), null, null);

            main.getServer().broadcast(ChatColor.GRAY + created.getReporter() + " (Web) reported a new ban.", "moderatorgui.viewreported");
            main.getServer().broadcast(reportString, "moderatorgui.viewreported");

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("u")) {
            Unbans row = new Unbans();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());

            main.getDatabase().save(row);

            Unbans created = main.getDatabase().find(Unbans.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.UNBAN, created.getReporter(), created.getReported());
            main.performCommands("unban", Bukkit.getConsoleSender(), row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), null, null);

            String reportString = main.formatReport(main.displayStrings[5], row.getId(), row.getReported(), row.getReporter(), row.getReason(), row.getTimestamp(), null, null);

            main.getServer().broadcast(ChatColor.GRAY + created.getReporter() + " (Web) reported a new unban.", "moderatorgui.viewreported");
            main.getServer().broadcast(reportString, "moderatorgui.viewreported");

            dop.writeBytes("true");

            return this;
        }

        throw new WebGuiException(new ErrorResponse(ErrorType.NotAcceptable));
    }

    private void report(int id, ReportType type, String reporter, String target) {
        Lists listRow = new Lists();

        listRow.setReportId(id);
        listRow.setType(type.getId());
        listRow.setReporter(reporter);
        listRow.setTarget(target);

        main.getDatabase().save(listRow);
    }
}
