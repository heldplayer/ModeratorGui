package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;

import me.heldplayer.ModeratorGui.ModeratorGui;

public class ThreadWebserver extends Thread {
	private ServerSocket serverSocket = null;
	public boolean running = false;
	public static ThreadWebserver instance;
	private HashSet<String> sessions;
	private Random rand;
	private final int port;
	private final String host;

	public ThreadWebserver(int port, String host) {
		super("ModeratorGui server thread");

		instance = this;
		sessions = new HashSet<String>();
		rand = new Random();
		this.port = port;
		this.host = host;

		this.setDaemon(true);
	}

	public void startup() {
		this.start();
		running = true;
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

		if (sessions.contains(session)) {
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
			ModeratorGui.instance.getLogger().info("Starting server on " + (host != "" ? host : "*") + ":" + port);

			InetAddress adress = null;

			if (host.length() > 0) {
				InetAddress.getByName(host);
			}

			serverSocket = new ServerSocket(port, 0, adress);
		} catch (Exception ex) {
			ModeratorGui.instance.getLogger().severe("**** FAILED TO BIND TO PORT");
			ModeratorGui.instance.getLogger().severe("The exception was: " + ex.toString());
			ModeratorGui.instance.getLogger().severe("Perhaps a server is already running on that port?");
			return;
		}

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
