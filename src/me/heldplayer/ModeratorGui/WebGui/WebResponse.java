package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public abstract class WebResponse {
	protected PipedInputStream pip;
	protected PipedOutputStream pop;
	protected DataOutputStream dop;

	public WebResponse() throws IOException {
		pop = new PipedOutputStream();
		pip = new PipedInputStream(pop, 16384);
		dop = new DataOutputStream(pop);
	}

	public abstract WebResponse writeResponse(RequestFlags flags)
			throws IOException;

	public void flush(DataOutputStream stream) throws IOException {
		try {
			while (pip.available() > 0) {
				int bits = pip.read();
				stream.write(bits);
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			try {
				dop.close();
				pop.close();
				pip.close();
			} catch (IOException ex) {
			}
		}
	}
}
