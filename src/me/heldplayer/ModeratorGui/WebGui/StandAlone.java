package me.heldplayer.ModeratorGui.WebGui;

public class StandAlone {

	public static void main(String[] args) {
		ThreadWebserver server = new ThreadWebserver();
		
		while(server.running){
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
