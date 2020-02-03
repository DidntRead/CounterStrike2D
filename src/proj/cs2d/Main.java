package proj.cs2d;

import java.net.Socket;

import proj.cs2d.map.ColoredCool;
import proj.cs2d.server.Server;

public class Main {
	public static void main(String[] args) throws Exception {
		Server local = new Server("Local", new ColoredCool(), 1, 5000, false, true);
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				local.start();
			}
		}, "LocalServerThread");
		serverThread.start();
		Socket sock = new Socket("localhost", 5000);
		Game game = new Game(sock, "Player");
		System.out.println("Game created");
		game.start();
		System.out.println("Game stopped");
		local.stop();
		serverThread.join();
	}
}
