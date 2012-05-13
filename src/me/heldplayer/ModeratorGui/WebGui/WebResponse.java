package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class WebResponse {
	public abstract void writeResponse(DataOutputStream stream, RequestFlags flags) throws IOException ;
}
