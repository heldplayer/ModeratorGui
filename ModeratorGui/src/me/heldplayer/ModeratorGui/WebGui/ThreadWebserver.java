package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadWebserver extends Thread {
	private ServerSocket serverSocket = null;
	public boolean running = true;

	public ThreadWebserver() {
		super("ModeratorGui server thread");

		this.setDaemon(true);
		this.start();
	}

	public synchronized void disconnect() {
		running = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Binding to port 8273...");

			serverSocket = new ServerSocket(8273);
		} catch (Exception ex) {
			System.err.println("Fatal error: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}

		System.out.println("Port bound!");

		while (running) {
			try {
				Socket socket = serverSocket.accept();
				
				new ThreadHttpResponse(socket);
				
				Thread.sleep(10L);
			} catch (IOException e) {
			} catch (InterruptedException e) {
			}
		}
	}
}
