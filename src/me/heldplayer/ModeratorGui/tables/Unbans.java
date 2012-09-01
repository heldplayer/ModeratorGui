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
@Table(name = "mgui_unbans")
public class Unbans {

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
	private String reason;

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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void toData(DataOutputStream DOS) throws IOException {
		DOS.writeInt(id);
		DOS.writeUTF(reporter);
		DOS.writeUTF(reported);
		DOS.writeUTF(reason);
		DOS.writeLong(timestamp);
	}

	public static Unbans fromData(DataInputStream DIS) throws IOException {
		Unbans row = new Unbans();

		row.id = DIS.readInt();
		row.reporter = DIS.readUTF();
		row.reported = DIS.readUTF();
		row.reason = DIS.readUTF();
		row.timestamp = DIS.readLong();

		return row;
	}

}
