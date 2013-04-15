
package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;

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

    public ReportResponse(String session, String type, String reported, String reason, String previousRank, String newRank) throws IOException {
        super();

        this.session = session;
        this.type = type;
        this.reported = reported;
        this.reason = reason;
        this.previousRank = previousRank;
        this.newRank = newRank;

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

            ModeratorGui.instance.getDatabase().save(row);

            Issues created = ModeratorGui.instance.getDatabase().find(Issues.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.ISSUE, created.getReporter(), created.getReported());

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

            ModeratorGui.instance.getDatabase().save(row);

            Promotions created = ModeratorGui.instance.getDatabase().find(Promotions.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.PROMOTE, created.getReporter(), created.getReported());

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

            ModeratorGui.instance.getDatabase().save(row);

            Demotions created = ModeratorGui.instance.getDatabase().find(Demotions.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.DEMOTE, created.getReporter(), created.getReported());

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("b")) {
            Bans row = new Bans();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());

            ModeratorGui.instance.getDatabase().save(row);

            Bans created = ModeratorGui.instance.getDatabase().find(Bans.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.BAN, created.getReporter(), created.getReported());

            dop.writeBytes("true");

            return this;
        }
        if (this.type.equals("u")) {
            Unbans row = new Unbans();
            row.setReason(reason);
            row.setReported(reported);
            row.setReporter(ThreadWebserver.instance.getSessionOwner(this.session));
            row.setTimestamp(System.currentTimeMillis());

            ModeratorGui.instance.getDatabase().save(row);

            Unbans created = ModeratorGui.instance.getDatabase().find(Unbans.class).where().eq("timestamp", row.getTimestamp()).findUnique();

            report(created.getId(), ReportType.UNBAN, created.getReporter(), created.getReported());

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

        ModeratorGui.instance.getDatabase().save(listRow);
    }
}
