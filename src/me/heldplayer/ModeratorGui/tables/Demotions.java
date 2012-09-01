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
@Table(name = "mgui_demotions")
public class Demotions {

	@Id
	private int id;

	@Length(max = 16)
	@NotNull
	private String demoter;

	@Length(max = 16)
	@NotNull
	private String demoted;

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

	public String getDemoter() {
		return demoter;
	}

	public void setDemoter(String demoter) {
		this.demoter = demoter;
	}

	public String getDemoted() {
		return demoted;
	}

	public void setDemoted(String demoted) {
		this.demoted = demoted;
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

	public void toData(DataOutputStream DOS) throws IOException {
		DOS.writeInt(id);
		DOS.writeUTF(demoter);
		DOS.writeUTF(demoted);
		DOS.writeUTF(reason);
		DOS.writeUTF(prevRank);
		DOS.writeUTF(newRank);
		DOS.writeLong(timestamp);
	}

	public static Demotions fromData(DataInputStream DIS) throws IOException {
		Demotions row = new Demotions();

		row.id = DIS.readInt();
		row.demoter = DIS.readUTF();
		row.demoted = DIS.readUTF();
		row.reason = DIS.readUTF();
		row.prevRank = DIS.readUTF();
		row.newRank = DIS.readUTF();
		row.timestamp = DIS.readLong();

		return row;
	}

}
