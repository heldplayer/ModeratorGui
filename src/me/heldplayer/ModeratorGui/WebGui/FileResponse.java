package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileResponse extends WebResponse {
	private File file;

	public FileResponse(File file) {
		this.file = file;
	}

	@Override
	public void writeResponse(DataOutputStream stream, RequestFlags flags) throws IOException {
		Extension extension = Extension.fromFileName(file.getName());

		stream.writeBytes("HTTP/1.0 200 OK\r\n");
		stream.writeBytes("Connection: close\r\n");
		stream.writeBytes("Server: ModeratorGui\r\n");
		stream.writeBytes("Content-Type: " + extension.type + "\r\n");
		stream.writeBytes("\r\n");

		FileInputStream input = new FileInputStream(file);

		if (flags.method.hasBody) {
			while (true) {
				int b = input.read();
				if (b == -1) {
					break;
				}
				stream.write(b);
			}
		}

		input.close();
	}

	private static enum Extension {
		TextPlain("text/plain"),
		TextHtml("text/html"),
		TextCss("text/css"),
		TextJavascript("text/javascript");
		public final String type;

		private Extension(String type) {
			this.type = type;
		}

		public static Extension fromFileName(String name) {
			name = name.toLowerCase();

			if (name.endsWith("htm") || name.endsWith("html") || name.endsWith("xhtm") || name.endsWith("xhtml"))
				return TextHtml;
			if (name.endsWith("css"))
				return TextCss;
			if (name.endsWith("js"))
				return TextJavascript;

			return TextPlain;
		}
	}
}
