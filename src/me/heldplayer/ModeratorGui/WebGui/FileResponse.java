package me.heldplayer.ModeratorGui.WebGui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileResponse extends WebResponse {
	private File file;

	public FileResponse(File file) throws IOException {
		super();
		this.file = file;
	}

	@Override
	public WebResponse writeResponse(RequestFlags flags) throws IOException {
		Extension extension = Extension.fromFileName(file.getName());

		dop.writeBytes("HTTP/1.0 200 OK\r\n");
		dop.writeBytes("Connection: close\r\n");
		dop.writeBytes("Server: ModeratorGui\r\n");
		dop.writeBytes("Content-Type: " + extension.type + "\r\n");
		dop.writeBytes("\r\n");

		FileInputStream input = new FileInputStream(file);

		if (flags.method.hasBody) {
			while (true) {
				int b = input.read();
				if (b == -1) {
					break;
				}
				dop.write(b);
			}
		}

		input.close();

		return this;
	}

	private static enum Extension {
		TextPlain("text/plain"), TextHtml("text/html"), TextCss("text/css"), TextJavascript("text/javascript");
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
