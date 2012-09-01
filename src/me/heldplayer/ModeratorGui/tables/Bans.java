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
@Table(name = "mgui_bans")
public class Bans {

	@Id
	private int id;

	@Length(max = 16)
	@NotNull
	private String banner;

	@Length(max = 16)
	@NotNull
	private String banned;

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

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getBanned() {
		return banned;
	}

	public void setBanned(String banned) {
		this.banned = banned;
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
		DOS.writeUTF(banner);
		DOS.writeUTF(banned);
		DOS.writeUTF(reason);
		DOS.writeLong(timestamp);
	}

	public static Bans fromData(DataInputStream DIS) throws IOException {
		Bans row = new Bans();

		row.id = DIS.readInt();
		row.banner = DIS.readUTF();
		row.banned = DIS.readUTF();
		row.reason = DIS.readUTF();
		row.timestamp = DIS.readLong();

		return row;
	}

}
