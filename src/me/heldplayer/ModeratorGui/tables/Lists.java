
package me.heldplayer.ModeratorGui.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "mgui_lists")
public class Lists {

    @Id
    private int id;

    @NotNull
    private int type;

    @NotNull
    private int reportId;

    @Length(max = 16)
    @NotNull
    private String reporter;

    @Length(max = 16)
    @NotNull
    private String target;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
