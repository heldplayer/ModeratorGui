package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;

public class ThreadWebserver extends Thread {
	private ServerSocket serverSocket = null;
	public boolean running = true;
	public static ThreadWebserver instance;
	private HashSet<String> sessions;
	private Random rand;

	public ThreadWebserver() {
		super("ModeratorGui server thread");

		instance = this;
		sessions = new HashSet<String>();
		rand = new Random();

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

	public String createSession() {
		int response = rand.nextInt();
		
		String session = Integer.toHexString(response);
		
		if(sessions.contains(session)){
			return null;
		}
		
		sessions.add(session);
		
		return session;
	}

	public boolean sessionAllowed(String session) {
		return sessions.contains(session);
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
