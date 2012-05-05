package me.heldplayer.ModeratorGui.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "mgui_issues")
public class Unbans {

	@Id
	private int id;

	@Length(max = 16)
	@NotNull
	private String unbanner;

	@Length(max = 16)
	@NotNull
	private String unbanned;

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

	public String getUnbanner() {
		return unbanner;
	}

	public void setUnbanner(String banner) {
		this.unbanner = banner;
	}

	public String getUnbanned() {
		return unbanned;
	}

	public void setUnbanned(String banned) {
		this.unbanned = banned;
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
}
