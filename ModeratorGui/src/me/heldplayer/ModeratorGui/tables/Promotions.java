package me.heldplayer.ModeratorGui.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;

@Entity()
@Table(name = "mgui_promotions")
public class Promotions {

	@Id
	private int id;

	@Length(max = 16)
	@NotNull
	private String promoter;

	@Length(max = 16)
	@NotNull
	private String promoted;

	@Length(max = 256)
	@NotNull
	private String reason;

	@Length(max = 16)
	@NotNull
	private String prevRank;

	@Length(max = 16)
	@NotNull
	private String newRank;

	@NotNull
	private long timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPromoter() {
		return promoter;
	}

	public void setPromoter(String promoter) {
		this.promoter = promoter;
	}

	public String getPromoted() {
		return promoted;
	}

	public void setPromoted(String promoted) {
		this.promoted = promoted;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPrevRank() {
		return prevRank;
	}

	public void setPrevRank(String prevRank) {
		this.prevRank = prevRank;
	}

	public String getNewRank() {
		return newRank;
	}

	public void setNewRank(String newRank) {
		this.newRank = newRank;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
