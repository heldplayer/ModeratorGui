package me.heldplayer.ModeratorGui.tables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "mgui_issues")
public class Issues {

	@Id
	private int id;

	@Length(max = 16)
	@NotNull
	private String reporter;

	@Length(max = 16)
	@NotNull
	private String reported;

	@Length(max = 256)
	@NotNull
	private String issue;

	private boolean isClosed = false;

	@NotNull
	private long timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public String getReported() {
		return reported;
	}

	public void setReported(String reported) {
		this.reported = reported;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public void toData(DataOutputStream DOS) throws IOException {
		DOS.writeInt(id);
		DOS.writeUTF(reporter);
		DOS.writeUTF(reported);
		DOS.writeUTF(issue);
		DOS.writeLong(timestamp);
		DOS.writeBoolean(isClosed);
	}

	public static Issues fromData(DataInputStream DIS) throws IOException {
		Issues row = new Issues();

		row.id = DIS.readInt();
		row.reporter = DIS.readUTF();
		row.reported = DIS.readUTF();
		row.issue = DIS.readUTF();
		row.timestamp = DIS.readLong();
		row.isClosed = DIS.readBoolean();

		return row;
	}

}
